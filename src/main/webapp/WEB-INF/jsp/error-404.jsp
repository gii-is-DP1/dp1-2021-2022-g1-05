<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="flippingboards" tagdir="/WEB-INF/tags" %>
<!-- %@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %-->

<flippingboards:layout pageName="home">
    <h2>There is no page here!</h2>
    <p>We're sorry, but the page you requested is not available in FlippingBoards</p>
    <p>${exception}</p>
    <p>${errorcode}</p>

</flippingboards:layout>
