<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ParchisYOca" tagdir="/WEB-INF/tags" %>

<ParchisYOca:layout pageName="ludoSumDice">
    <h2>Please select da chip</h2>
    <div class="container">
        <div class="row">
            <div class="tablero">
                <ParchisYOca:ludoBoard ludoBoard="${ludoBoard}"/>

            </div>
        </div>
    </div>

</ParchisYOca:layout>
