<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ParchisYOca" tagdir="/WEB-INF/tags" %>

<ParchisYOca:layout pageName="createGooseMatch">
    <h1>Please enter a valid match code</h1>

    <form:form modelAttribute="match" class="form-horizontal" id="match-code-form">
        <div class="form-group has-feedback">
            <parchisYOca:inputField label="Match Code" name="matchCode"/>
        </div>
        <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
                <button class="btn btn-default" type="submit">Join lobby</button>
            </div>
        </div>
    </form:form>

</ParchisYOca:layout>
