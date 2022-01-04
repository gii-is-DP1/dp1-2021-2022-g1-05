<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="flippingboards" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<!--  >%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%-->
<%@ attribute name="name" required="true" rtexprvalue="true"
	description="Name of the active menu: home, owners, vets or error"%>

<nav class="navbar navbar-default" role="navigation">
	<div class="container">
		<div class="navbar-header">
			<a class="navbar-brand"
				href="<spring:url value="/" htmlEscape="true" />">
                <span></span>
            </a>
			<button type="button" class="navbar-toggle" data-toggle="collapse"
				data-target="#main-navbar">
				<span class="sr-only"><os-p>Toggle navigation</os-p></span> <span
					class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
		</div>
		<div class="navbar-collapse collapse" id="main-navbar">
			<ul class="nav navbar-nav">

				<flippingboards:menuItem active="${name eq 'home'}" url="/"
					title="home page">
					<span class="glyphicon glyphicon-home" aria-hidden="true"></span>
					<span>Home</span>
				</flippingboards:menuItem>

                <sec:authorize access="hasAuthority('admin')">
                    <flippingboards:menuItem active="${name eq 'owners'}" url="/players"
                        title="find players">
                        <span class="glyphicon glyphicon-user" aria-hidden="true"></span>
                        <span>Players</span>
                    </flippingboards:menuItem>
                    <flippingboards:menuItem active="${name eq 'ludoMatches'}" url="/ludoMatches"
                         title="find ludo matches">
                        <span class="glyphicon glyphicon-list" aria-hidden="true"></span>
                        <span>Ludo</span>
                    </flippingboards:menuItem>
                    <flippingboards:menuItem active="${name eq 'gooseMatches'}" url="/gooseMatches"
                                             title="find goose matches">
                        <span class="glyphicon glyphicon-list" aria-hidden="true"></span>
                        <span>Goose</span>
                    </flippingboards:menuItem>

                    <flippingboards:menuItem active="${name eq 'achievements'}" url="/achievements"
                                             title="global achievement list">
                        <span class="glyphicon glyphicon-star" aria-hidden="true"></span>
                        <span>Achievements</span>
                    </flippingboards:menuItem>
                </sec:authorize>



			</ul>


			<ul class="nav navbar-nav navbar-right">
				<sec:authorize access="!isAuthenticated()">
					<li><a href="<c:url value="/login" />">Login</a></li>
					<li><a href="<c:url value="/users/new" />">Register</a></li>
				</sec:authorize>
				<sec:authorize access="isAuthenticated()">
					<li class="dropdown"><a href="#" class="dropdown-toggle"
						data-toggle="dropdown"> <span class="glyphicon glyphicon-user"></span>
							<strong><sec:authentication property="name" /></strong> <span
							class="glyphicon glyphicon-chevron-down"></span>
					</a>
						<ul class="dropdown-menu">
							<li>
								<div class="navbar-login">
									<div class="row">
										<div class="col-lg-4">
											<p class="text-center">
												<span class="glyphicon glyphicon-user icon-size"></span>
											</p>
										</div>
										<div class="col-lg-8" style="margin-left:15%;  margin-right: 15%">
											<p class="text-left">
												<strong><sec:authentication property="name" /></strong>
                                                <a href="<c:url value="/players/ownProfile"/>" class="btn btn-primary btn-block btn-sm">My profile</a>
											</p>
                                            <p class="text-left">
                                                <a href="<c:url value="/logout" />"
                                                   class="btn btn-primary btn-block btn-sm" style="margin-top: 2%">Logout</a>
                                            </p>
										</div>
									</div>
								</div>
							</li>
							<li class="divider"></li>
<!--

-->
						</ul></li>
				</sec:authorize>
			</ul>
		</div>



	</div>
</nav>
