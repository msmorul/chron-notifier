<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Chronopolis Notification Server</title>
        <LINK href="960.css" rel="stylesheet" type="text/css">
        <link type="text/css" href="css/ui-lightness/jquery-ui-1.8.20.custom.css" rel="Stylesheet" />	
        <LINK href="style.css" rel="stylesheet" type="text/css">
        <script type="text/javascript" src="jquery.js"></script>          

        <script type="text/javascript" src="jquery-ui-1.8.20.custom.min.js"></script>          
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

                        <div class="grid_3"><a href="#" onclick='$( "#details-${item.id}" ).dialog({modal: true})'>${item.identifier}</a> <a href="resources/status/${item.identifier}/manifest"><img alt="Download Manifest" src="emblem-downloads.png"></a></div>
                        <div class="grid_2">
                            <c:if test="${item.requestType == 0}">Ingest Request</c:if>
                            <c:if test="${item.requestType == 1}">Complete Restore</c:if>
                            <c:if test="${item.requestType == 2}">Item Restore</c:if>
                        </div>

                        <div class="grid_3">
                            <c:if test="${item.status == 0}">Ticket Open</c:if>
                            <c:if test="${item.status == 1}">Ticket Finished</c:if>
                            <c:if test="${item.status == 2}">Ticket closed with errors</c:if>
                        </div>
                        <div><c:if test="${item.status == 0}"><a href="#" onclick='$( "#d-${item.id}" ).dialog({modal: true})'>Update</a></c:if></div>
                        <div class="clear"></div>

                        

                        <div class="ticket_details" id="details-${item.id}" title="${item.identifier}">
                            <ul>
                                <li><c:if test="${item.requestType == 0}">Ingest Request</c:if>
                                    <c:if test="${item.requestType == 1}">Complete Restore</c:if>
                                    <c:if test="${item.requestType == 2}">Item Restore</c:if>
                                </li>
                                <li>
                                    <c:if test="${item.status == 0}">Ticket Open</c:if>
                                    <c:if test="${item.status == 1}">Ticket Finished</c:if>
                                    <c:if test="${item.status == 2}">Ticket closed with errors</c:if>
                                    </li>
                                    <li>${item.spaceId}</li>
                                    <li>${item.accountId}</li>
                                    <li>${item.itemId}</li>
                                    <li>${item.statusMessage}</li>


                            </ul>
                        </div>

                        <c:if test="${item.status == 0}">
                            <div class="ticket_form_block" id="d-${item.id}" title="Update Ticket">
                                <form action="resources/status/${item.identifier}" id="f-${item.identifier}"method="post">
                                    

                                    <div class=" ticket_status_form_block">
                                        <div><input type="checkbox" name="isFinished" value="true" >Finished</div>
                                        <div><input type="checkbox" name="isError" value="true" >Errors</div>
                                    </div>

                                    <div class=""><textarea name="description" cols="30" rows="5"></textarea></div>
                                    <div class="submit_form_link"><a href="#" onclick='$.post("resources/status/${item.identifier}", $("#f-${item.identifier}").serialize());location.reload()'>Update Ticket</a></div>

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
