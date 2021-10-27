<%@ page session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="parchisYOca" tagdir="/WEB-INF/tags" %>


<parchisYOca :layout pageName="Events">
    <jsp:attribute name="customScript">
        <script>
            $(function () {
                $("#start").datepicker({dateFormat: 'yy/mm/dd'});
                $("#end").datepicker({dateFormat: 'yy/mm/dd'});
            });
        </script>
    </jsp:attribute>
    <jsp:body>
        <h2>Achievement</h2>

        <form:form modelAttribute="achievement" class="form-horizontal" action="/achievements/save">
            <div class="form-group has-feedback">
                <parchisYOca:inputField label="name" name="name"/>
                <parchisYOca:inputField label="description" name="description"/>
ç

            </div>

            <div class="form-group">
                <div class="col-sm-offset-2 col-sm-10">
                    <input type="hidden" name="id" value="${achievement.id}"/>
                    <button class="btn btn-default" type="submit">Save achievement</button>
                </div>
            </div>
        </form:form>


    </jsp:body>

</parchisYOca:layout>
