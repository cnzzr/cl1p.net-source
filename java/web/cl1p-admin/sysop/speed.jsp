<%@ page import="com.diodesoftware.scb.speed.SpeedRecorder" %>
<html>
<head>
    <title>Speed Watch</title>
</head>
<body>
<textarea rows="80" cols="80" style="width:100%;">
    <%= SpeedRecorder.printSummry() %>
</textarea>

</body>
</html>