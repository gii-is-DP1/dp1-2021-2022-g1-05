<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ParchisYOca" tagdir="/WEB-INF/tags" %>

<ParchisYOca:layout pageName="createLudoMatch">
    <h1>Please enter a valid match code</h1>

    <br>

    <form:form modelAttribute="match" class="form-horizontal" id="match-code-form">
        <div class="form-group has-feedback" style="margin-left: 2%">
            <label for="matchCode" style="margin-right: 0.5%">Matchcode:</label>
            <input type="text" name="matchCode" id="matchCode" placeholder="Enter a matchCode">
        </div>
        <div class="form-group">
            <div class="col-sm col-sm-10" style="margin-left: 2%">
                <button class="btn btn-default" type="submit">Join lobby</button>
            </div>
        </div>
    </form:form>

</ParchisYOca:layout>
