<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="flippingboards" tagdir="/WEB-INF/tags" %>

<flippingboards:layout pageName="Users">
    <h2>
        <c:if test="${owner['new']}">New </c:if> User
    </h2>
    <form:form modelAttribute="owner" class="form-horizontal" id="add-owner-form">
        <div class="form-group has-feedback">
            <flippingboards:inputField label="Email" name="email"/>
            <flippingboards:inputField label="Username" name="user.username"/>
            <flippingboards:inputField label="Password" name="user.password"/>
            <flippingboards:inputField label="AvatarURL" name="avatarURL"
        </div>
        <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
                <c:choose>
                    <c:when test="${owner['new']}">
                        <button class="btn btn-default" type="submit">Add Owner</button>
                    </c:when>
                    <c:otherwise>
                        <button class="btn btn-default" type="submit">Update Owner</button>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </form:form>
</flippingboards:layout>
