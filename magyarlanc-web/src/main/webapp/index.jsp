<%@ page import="hu.u_szeged.magyarlanc.webservice.endpoint.MagyalancWebServiceInterface" %>
<%@ page import="hu.u_szeged.magyarlanc.webservice.endpoint.MagyarlancWebServiceImpService" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>

<form action="index.jsp" method="POST">
    <input type="text" name="content">
</form>

<%
    String content = request.getParameter("content");
    MagyarlancWebServiceImpService magyarlancWebServiceImpService = new MagyarlancWebServiceImpService();
    MagyalancWebServiceInterface magyalancWebServiceInterface = magyarlancWebServiceImpService.getMagyarlancWebServiceImpPort();
    String parsed = null;
    if (content != null) {
        parsed = magyalancWebServiceInterface.parse(content);
    }
    System.out.println(parsed);
%>

</body>
</html>


