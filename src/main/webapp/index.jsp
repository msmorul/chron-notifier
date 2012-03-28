<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Chronopolis Notification Server</title>
        <LINK href="960.css" rel="stylesheet" type="text/css">
        <LINK href="style.css" rel="stylesheet" type="text/css">
         <script type="text/javascript" src="jquery.js"></script>          
    </head>
    <body>
    <body>
        <div class="container_12">
            <div class="grid_12" id="header">
                <div class="title"><h1>Chronopolis Notification Server</h1></div>

            </div>
            <div class="grid_10 ">


                <c:forEach var="item" items="${ticketList}">
                    <div class="grid_10 ticket_block">

                        <div class="grid_2">Ticket ID</div>
                        <div class="grid_2">${item.identifier}</div>
                        <div class="grid_4">
                            <c:if test="${item.status == 1}">Finished</c:if>
                            <c:if test="${item.status == 2}">Errors</c:if>
                        </div>
                        <div class="clear"></div>
                        
                        <div class="grid_9 push_1 ticket_comments"><pre>${item.statusMessage}</pre></div>
                        <div class="clear"></div>
                        
                        <c:if test="${item.status == 0}">
                            <div class="grid_10 ticket_form_block">
                            <form action="resources/status/${item.identifier}" id="f-${item.identifier}"method="post">
                                <div class="grid_2">Set Status</div>
                                <div class="grid_4">Comments</div>
                                <div class="clear"></div>

                                <div class="grid_2 ticket_status_form_block">
                                    <div><input type="checkbox" name="isFinished" value="true" >Finished</div>
                                    <div><input type="checkbox" name="isError" value="true" >Errors</div>
                                    <div class="submit_form_link"><a href="#" onclick='$.post("resources/status/${item.identifier}", $("#f-${item.identifier}").serialize());location.reload()'>Update Ticket</a></div>
                                </div>

                                <div class="grid_4"><textarea name="description" cols="40" rows="10"></textarea></div>
                                
                            </form>
                            </div>
                        </c:if>
                        
                        <div class="clear"></div>

                    </div>
                </c:forEach>

                <div class="clear"></div>
                <div class="grid_12"  id="footer">

                </div>
            </div>
    </body>
</body>
</html>
