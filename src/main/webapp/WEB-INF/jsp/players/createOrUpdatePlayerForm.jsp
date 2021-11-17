<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="parchisYOca" tagdir="/WEB-INF/tags" %>

<parchisYOca:layout pageName="players">
    <h2>
        <c:if test="${player['new']}">New </c:if> Player
    </h2>
    <form:form modelAttribute="player" class="form-horizontal" id="add-owner-form">
        <div class="form-group has-feedback">
            <parchisYOca:inputField label="Username" name="user.username"/>
            <parchisYOca:inputField label="Password" name="user.password"/>
            <parchisYOca:inputField label="Email" name="email"/>
            <form:input type="hidden" path="id"/>

        </div>
        <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
                <c:choose>
                    <c:when test="${player['new']}">
                        <button class="btn btn-default" type="submit">Add Player</button>
                    </c:when>
                    <c:otherwise>
                        <button class="btn btn-default" type="submit">Update Player</button>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </form:form>
</parchisYOca:layout>
