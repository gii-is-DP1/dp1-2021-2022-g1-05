<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="flippingboards" tagdir="/WEB-INF/tags" %>
<!-- %@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %-->  

<flippingboards:layout pageName="home">
    <div class="row">
        <div class="col-md text-center">
            <img src="/resources/images/Logo.png" style="width: 25%"/>
        </div>
    </div>
    <div class="row" style="margin-top: 3%">
        <div class="col-md-3"></div>
        <div class="col-md-3 text-center" style="padding-left: 80px">
            <button type="button" class="btn btn-primary btn-lg btn-block">Create ludo game</button>
            <button type="button" class="btn btn-primary btn-lg btn-block">Join ludo game</button>
        </div>
        <div class="col-md-3 text-center" style="padding-right: 80px">
            <a href="/gooseMatches/new"><button type="button" class="btn btn-info btn-lg btn-block">Create goose game</button></a>
            <button type="button" class="btn btn-info btn-lg btn-block">Join goose game</button>
        </div>
        <div class="col-md-3"></div>
    </div>

</flippingboards:layout>
