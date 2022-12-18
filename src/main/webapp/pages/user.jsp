<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page isELIgnored="false"%>

<fmt:setLocale value="${param.lang}" />

<html lang="${param.lang}">
  <head>
       <meta charset="utf-8">
       <meta name="viewport" content="width=device-width, initial-scale=1">
       <link rel="stylesheet" href="./styles/bootstrap-reboot.css">
       <link rel="stylesheet" href="./styles/bootstrap.css">
       <link rel="stylesheet" href="./styles/style.css">
       <title> Taxi </title>
    </head>
    <body class="container vh-100 background">
          <nav class="navbar navbar-expand-md navbar-light background-orange">
              <div class="container-fluid">
                <div class="dropdown">
                  <button class="btn btn-warning dropdown-toggle" id="content-Language" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                      English
                  </button>
                  <ul class="dropdown-menu dropdown-menu-orange">
                    <li><fmt:setLocale value="ru"/>ru </li>
                    <li><fmt:setLocale value="en"/>en</li>
                  </ul>
          </nav>
      <form action="user" method="post">
          <div class="input-group mb-3">
             <select class="form-select" aria-label="Default select example">
               <option selected>Open this select menu</option>
               <%
               for (DaysOfWeekEnum day : DaysOfWeekEnum.values()) {
                   System.out.println(day);
               }%>
             </select>
            <button name="act" value= "findCar" type="submit" class="btn btn-primary background-orange-button">Find car</button>
          </div>
      </form>
      <form action="user" method="post">
          <div class="input-group mb-3">
              <button name="act" value= "createOrder" type="submit" class="btn btn-primary background-orange-button">Create order</button>
          </div>
      </form>
      <script src="./js/bootstrap.bundle.js"></script>
    </body>
</html>