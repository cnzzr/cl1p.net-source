import cgi
import datetime
import wsgiref.handlers
import os
from google.appengine.ext.webapp import template

from google.appengine.ext import db
from google.appengine.api import users
from google.appengine.ext import webapp

class Html():
	message = ''
	restrictViewsChecked = ''
	error = False
	


class Clip(db.Model):
	uri = db.StringProperty()
	content = db.TextProperty()
	title = db.StringProperty('clip.com')
	readOnly = db.BooleanProperty(False)
	password = db.StringProperty('')
	restrictViews = db.BooleanProperty(False)
	rows = db.IntegerProperty(20)
	created = db.DateTimeProperty(auto_now_add=True)
	updated = db.DateTimeProperty(auto_now=True)
	uploadedFile = db.BlobProperty()
	uploadedFileName = db.StringProperty();
	
		

class MainHandler(webapp.RequestHandler):
	def findClip(self):
		query = db.Query(Clip)
		query.filter('uri =', self.request.path_qs)		
		clip = query.get()
		if(clip == None):
			clip = Clip()
			clip.content = ''
			clip.password= ''
		return clip
	
	def postClip(self):
		clip = self.findClip()
		html = Html()
		loginString = self.request.get('login')
		login = False
		if(loginString == 'Yes'):
			login = True
		p1 = self.request.get('password');
		p2 = self.request.get('passwordVerify')
		clearPassword = self.request.get('clearPassword')
		restrictViews = self.request.get('restrictViews')
		#self.log("Restrict Views [" + restrictViews + "]")
		if(clip.password != ''):			
			if(p1 != clip.password):
				html.message = "Invalid password"
				html.error = True
		else:
			if(p1 != ''):
				if(p2 != p1):
					html.message = "Passwords don't match"
					html.error = True
				else:
					clip.password = p1
		
		if(html.error == False):
			if(login == False):
				content = self.request.get('ctrlcv')
				
				if(content != None):
					clip.content = content
				clip.uri = self.request.path_qs
				if(clearPassword == 'Yes'):
					clip.password = ''
				if(restrictViews == 'Yes'):
					#self.log("Set")
					clip.restrictViews = True
				clip.put()
		self.getClip()
	
	def log(self, m):
		self.response.out.write(m + '<br>')
		
	def getClip(self):
		clip = self.findClip()
		html = Html()
		p1 = self.request.get('password')
		if(clip.restrictViews == True):
			html.restrictViewsChecked = ' checked '
		mode = 1
		if(clip.password != ''):
			#self.log("Has password")
			mode = 2
			if(clip.restrictViews):
				#self.log("Restrict views")
				mode = 3
			if(p1 == clip.password):
				#self.log("Password matches")
				mode = 1
			else:
				#self.log("Password invalid")
				html.message = "Invalid password"
				
		if(clip.password != ''):	
			if(p1 != clip.password):
				if(p1 != ''):
					html.message = "Invalid password"
				html.error = True
		#self.log("Rendering " + str(mode))
		clip.uri = self.request.path_qs
		fileNumber = self.request.get('f');
		self.renderClip(clip, html, mode)
	
	def renderClip(self,clip, html, mode):
		template_file = 'cl1p_edit.html'
		if(mode == 2):
			template_file = 'cl1p_read_only.html'
		if (mode == 3):
			template_file = 'cl1p_enter_password.html'
		#self.log("Rendering [" + template_file + "] for mode [" + str(mode) + "]")
		path = os.path.join(os.path.dirname(__file__), template_file)
		dic = {'clip': clip, 'html' : html}
		self.response.out.write(template.render(path,  dic))
	
	def renderFile(self, file):
		path = os.path.join(os.path.dirname(__file__), file)
		self.response.out.write(template.render(path, None))
	
	def notAClip(self):
		uri = self.request.path_qs
		files = ['/index.html','/','/style.css']
		for f in files:
			if(f == uri):
				if(f == '/'):
					f = "index.html"
				else:
					f = f[1:]
				self.renderFile(f)
				return True
		return False
	
		
	def get(self):
		if(self.notAClip() == True):
			return
		self.getClip()
	
	def post(self):
		self.postClip()
	
		
application = webapp.WSGIApplication([
  ('/.*', MainHandler)
], debug=True)
		


def main():
  wsgiref.handlers.CGIHandler().run(application)

