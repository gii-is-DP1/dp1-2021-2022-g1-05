<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="flippingboards" tagdir="/WEB-INF/tags" %>
<!-- %@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %-->

<flippingboards:layout pageName="home">
    <h2>Go away!</h2>
    <p>We're sorry, but you are not allowed to access the page you requested.</p>
    <p>${exception}</p>
    <p>${errorcode}</p>

</flippingboards:layout>
