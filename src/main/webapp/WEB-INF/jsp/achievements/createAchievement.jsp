<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="parchisYOca" tagdir="/WEB-INF/tags" %>


<parchisYOca:layout pageName="Achievements">
    <h2>
        New achievement
    </h2>

    <form:form modelAttribute="achievement" class="form-horizontal" id="add-achievement-form">
    <div class="form-group has-feedback">
        <parchisYOca:inputField label="Name of the achievement" name="name"/>
        <div class="control-group">
            <parchisYOca:selectField name="description" label="Requirement to beat" names="${descriptions}" size="5"/>
        </div>
        <parchisYOca:inputField label="Number to beat" name="numberToBeat"/>

    </div>
    <div class="form-group">
        <div class="col-sm-offset-2 col-sm-10">
            <button class="btn btn-default" type="submit">Send creation</button>
        </div>
    </div>
    </form:form>

    <c:forEach items="${exceptions}" var="exception">
    <p><c:out value="${exception}"/></p>
    </c:forEach>

</parchisYOca:layout>
