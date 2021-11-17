<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="petclinic" tagdir="/WEB-INF/tags" %>

<petclinic:layout pageName="error">

    <spring:url value="/resources/images/congrats.png" var="gooseAward"/>
    <img src="${gooseAward}" style="width: 24%"/>

    <h2>Congrats! You broke the page.</h2>

    <p>${exception.message}</p>

</petclinic:layout>
