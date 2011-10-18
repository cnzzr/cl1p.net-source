package com.diodesoftware.scb.agents;

import com.diodesoftware.dbmapper.DBMapper;
import com.diodesoftware.dbmapper.PasswordEncrypter;
import com.diodesoftware.scb.tables.*;
import com.diodesoftware.scb.ClipUtil;
import com.diodesoftware.scb.filter.ClipFilter;
import com.diodesoftware.scb.GLOBAL;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Rob
 * Date: Jul 3, 2006
 * Time: 6:09:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserAgent {

    private static UserAgent instance = null;
    private DBMapper dbMapper;

    private Logger log = Logger.getLogger(UserAgent.class);

    private UserAgent(DBMapper mapper) {
        this.dbMapper = mapper;
    }

    public static synchronized void initalize(DBMapper dbMapper) {
        instance = new UserAgent(dbMapper);
    }

    public static UserAgent getInstance() {
        return instance;
    }

    public int createUser(String username, String password, String email, Connection con) {
        User user = load(username,con);
        if(user != null){
            return -1;
        }
        user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        try{
            dbMapper.save(user, con);
        }catch(Exception e){
            log.error("Error creating user", e);
        }
        return user.getNumber();
    }

    public User load(String username, Connection con) {
        User result = null;

        String sql = "Select * from User where Username = ?";
        try {
            PreparedStatement prepStmt = con.prepareStatement(sql);
            prepStmt.setString(1, username);
            ResultSet rs = prepStmt.executeQuery();
            if(rs.next())
                result = (User)dbMapper.loadSingle(User.class, rs);
            rs.close();
            prepStmt.close();
        } catch (SQLException e) {
            log.error("Error running SQL [" + sql + "]", e);
        }
        return result;
    }

    public User load(int userId, Connection con){
        return (User)dbMapper.load(User.class, userId, con);
    }

    public User login(String username, String password, Connection con){
        User result = null;
        String sql = "Select * from User where Username = ? and Password = ? and Disabled = 'N'";
        try{
            PreparedStatement prepStmt = con.prepareStatement(sql);
            String encryptedPassword = PasswordEncrypter.encrypt(password);
            prepStmt.setString(1, username);
            prepStmt.setString(2, encryptedPassword);
            ResultSet rs = prepStmt.executeQuery();
            if(rs.next()){
                result=  (User)dbMapper.loadSingle(User.class, rs);
            }
            rs.close();
            prepStmt.close();
        }catch(SQLException e){
            log.error("Error running sql [" + sql + "]",e);
        }
        return result;
    }

    public Clip[] getUserClips(User user, Connection con){
    List result = new ArrayList();
    String sql = "Select c.* from Clip c inner join UserClip u on u.ClipId = c.Number " +
        " where u.UserId = ? order by c.LastEdit DESC";
    try{
        PreparedStatement prepStmt = con.prepareStatement(sql);
        prepStmt.setInt(1, user.getNumber());
        ResultSet rs = prepStmt.executeQuery();
        while(rs.next()){
        result.add((Clip)dbMapper.loadSingle(Clip.class, rs));
        }
        rs.close();
        prepStmt.close();
    }catch(SQLException e){
        log.error("Error running sql [" + sql + "]",e);
    }
        Clip[] clips = new Clip[result.size()];
        result.toArray(clips);
        return clips;
    }

    public void addUserClip(int clipId, int userId, Connection con){
    String sql = "Select Number from UserClip where ClipId = ? and UserId = ?";
    try{
        PreparedStatement prepStmt = con.prepareStatement(sql);
        prepStmt.setInt(1, clipId);
        prepStmt.setInt(2, userId);
        ResultSet rs = prepStmt.executeQuery();
        boolean found = rs.next();
        rs.close();
        prepStmt.close();
        if(!found){
        UserClip uc = new UserClip();
        uc.setClipId(clipId);
        uc.setUserId(userId);
        dbMapper.save(uc,con);
        }
    }catch(SQLException e){
        log.error("Error running SQL [" + sql + "]",e);
    }
    }

    public void save(User user, Connection con){
        dbMapper.save(user, con);
    }

     public  boolean uploadLimitReached(User user, ServletContext context, Connection con){
        Clip[] clips = getUserClips(user, con);
        long used = 0l;
        for(int i = 0;i < clips.length; i++){
            String fileName = ClipFilter.uploadDirName(clips[i].getUri(), context );
            File file = new File(fileName);
            if(file.exists()){
                used += file.length();
            }
        }
        long max = (GLOBAL.UPLOAD_MAX_PRO * 1048576 );
        return used > max;
    }

    public Clip[] loadOwnedClips(int userId, Connection con){
	List list = new ArrayList();
	String sql = "Select c.* from Clip c inner join Owner o on c.ownerId = o.number where userId = " + userId + " ORDER by c.LastEdit DESC";
	try{
	    
	    PreparedStatement prepStmt = con.prepareStatement(sql);
	    //prepStmt.setInt(1, userId);
	    ResultSet rs = prepStmt.executeQuery(sql);
	    while(rs.next()){
		list.add(dbMapper.loadSingle(Clip.class, rs));
	    }
	    rs.close();
	    prepStmt.close();
	}catch(SQLException e){
	    log.error("Error running sql [" + sql + "]", e);
	}
	    Clip[] result = new Clip[list.size()];
	    list.toArray(result);
	    return result;
    }

    public List<User> searchUsers(Connection con, String username, String email, String number){
        String sql = "Select * from User";
        String conjuction = " WHERE";
        List args = new ArrayList();
        if(!ClipUtil.isBlank(username)){
            username = username.replace('*','%');
            sql += conjuction + " Username Like ?";
            conjuction = " AND";
            args.add(username);
        }
        if(!ClipUtil.isBlank(email)){
            email = email.replace('*','%');
            sql += conjuction + " Email Like ?";
            args.add(email);
            conjuction = " AND";
        }
        if(!ClipUtil.isBlank(number)){
            sql += conjuction + " Number = ?";
            args.add(Integer.parseInt(number));
            conjuction = " AND";
        }
        List<User> result = new ArrayList();
    	try{
    		PreparedStatement prepStmt = con.prepareStatement(sql);
            int i = 1;
            for(Object o : args){
                prepStmt.setObject(i, o);
                i++;
            }
            ResultSet rs = prepStmt.executeQuery();
    		while(rs.next()){
    			result.add((User)dbMapper.loadSingle(User.class, rs));
    		}
    		rs.close();
    		prepStmt.close();
    	}catch(SQLException e){
    		log.error("Error running SQL [" + sql + "]",e);
    	}
    	return result;        
    }
    
    public List<User> loadAllUsers(Connection con){
    	String sql = "Select * from User";
    	List<User> result = new ArrayList();
    	try{
    		PreparedStatement prepStmt = con.prepareStatement(sql);
    		ResultSet rs = prepStmt.executeQuery();
    		while(rs.next()){
    			result.add((User)dbMapper.loadSingle(User.class, rs));
    		}
    		rs.close();
    		prepStmt.close();
    	}catch(SQLException e){
    		log.error("Error running SQL [" + sql + "]",e);
    	}
    	return result;
    }

    public List<User> search(int id, String username, String email, Calendar createdFrom, Calendar createdTo, Connection con){
        String sql = "Select * from User";
        String concat = " where";
        ArrayList list = new ArrayList();
        if(id > 0){
            sql  += concat + " Number = ?";
            list.add(id);
            concat = " and";
        }
        if(username != null){
            sql += concat + " Username like ?";
            list.add("%" + username + "%");
            concat = " and";
        }
        if(email != null){
            sql += concat + " Email like ?";
            list.add("%" + email + "%");
            concat = " and";
        }
        if(createdFrom != null && createdTo != null){
            sql += concat + " Created between ? and ?";
            list.add(createdFrom.getTimeInMillis());
            list.add(createdTo.getTimeInMillis());
            concat = " and";
        }else if(createdFrom != null){
            sql += concat + " Created > ?";
            list.add(createdFrom.getTimeInMillis());
            concat = " and";
        }else if(createdTo != null){
            sql += concat + " Created < ?";
            list.add(createdTo.getTimeInMillis());
            concat = " and";
        }
        List<User> result =new ArrayList<User>();
        try{
  
            PreparedStatement prepStmt = con.prepareStatement(sql);
            int i = 1;
            Iterator iter = list.iterator();
            while(iter.hasNext()){
                prepStmt.setObject(i, iter.next());
                i++;
            }

            ResultSet rs = prepStmt.executeQuery();
            while(rs.next()){
                result.add((User)DBMapper.loadSingle(User.class, rs));
            }
            rs.close();
        }catch(SQLException e){
            log.error("Error running SQL [" + sql + "]",e);
        }
        return result;
    }

    public void sort(final String column, final boolean ascending, List<User> searchResult){

              Comparator comparator = new Comparator() {
                public int compare(Object o1, Object o2) {
                    User c1 = (User) o1;
                    User c2 = (User) o2;
                    if (column == null) {
                        return 0;
                    }
                    if (column.equals("number")) {
                        return ascending ?
                               (c1.getNumber() > c2.getNumber())?1:-1:
                               (c2.getNumber() > c1.getNumber())?1:-1;
                    } else if (column.equals("username")) {
                        return ascending ?
                               c1.getUsername().compareTo(c2.getUsername()) :
                               c2.getUsername().compareTo(c1.getUsername());
                    } else if (column.equals("email")) {
                        return ascending ?
                               c1.getEmail().compareTo(c2.getEmail()) :
                               c2.getEmail().compareTo(c1.getEmail());
                    } else if (column.equals("disabled")) {
                        return ascending ? c1.isDisabled()?1:-1:
                               c1.isDisabled()?1:-1;
                    }
                     else if (column.equals("created")) {
                        return ascending ?
                               c1.getCreated().compareTo(c2.getCreated()):
                               c2.getCreated().compareTo(c1.getCreated());
                    }
                    else return 0;
                }
            };
            Collections.sort(searchResult, comparator);
        }


}
