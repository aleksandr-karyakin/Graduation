<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<html>
<jsp:include page="fragments/headTag.jsp"/>

<body>
<jsp:include page="fragments/bodyHeader.jsp"/>

<div class="jumbotron py-0">
    <div class="container">
        <c:if test="${param.error}">
            <div class="error">${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}</div>
        </c:if>
        <c:if test="${not empty param.message}">
            <div class="message"><spring:message code="${param.message}"/></div>
        </c:if>
        <sec:authorize access="isAnonymous()">
            <div class="pt-4">
                <a class="btn btn-lg btn-success" href="register"><spring:message code="app.register"/> &raquo;</a>
                <button type="submit" class="btn btn-lg btn-primary" onclick="login('user@yandex.ru', 'password')">
                    <spring:message code="app.login"/> User
                </button>
                <button type="submit" class="btn btn-lg btn-primary" onclick="login('admin@gmail.com', 'admin')">
                    <spring:message code="app.login"/> Admin
                </button>
                <button type="submit" class="btn btn-lg btn-primary" onclick="login('manager@gmail.com', 'manager')">
                    <spring:message code="app.login"/> Manager
                </button>
            </div>
        </sec:authorize>
        <div class="lead py-4">
            <ul>Стек технологий:
                <li>Spring Framework, Spring Data JPA, Spring MVC, Spring Security, Spring Test</li>
                <li>Hibernate ORM, Hibernate Validator, HSQLDB, PostgreSQL</li>
                <li>JUnit, AssertJ, Hamcrest</li>
                <li>Jackson, EhCache, Logback, SLF4J</li>
                <li>Apache Tomcat, JSP, JSTL</li>
                <li>Jquery, Jquery Datatables, Bootstrap</li>
            </ul>
        </div>
    </div>
</div>
<div class="container lead pb-3">

    <p>Приложение с регистрацией/авторизацией и интерфейсом на основе ролей (USER, MANAGER, ADMIN).</p>

    <p><b>Администратор</b> может создавать/редактировать/удалять пользователей, изменять им роли, и временно отключать
        их.
    <p/>

    <p><b>Менеджеры</b> могут создавать/редактировать/удалять рестораны, которыми они управляют,
        а также вести список блюд для каждого ресторана. При этом они могут из общего списка блюд ресторана выбирать те
        из них, которые будут в меню сегодня и будут видны всем пользователям, в отличие от общего списка блюд.
    <p/>

    <p><b>Пользователи</b> могут управлять своим профилем, просматривать общий список ресторанов и их сегодняшнее меню,
        и голосовать за понравившийся ресторан.
        При этом у каждого пользователя только один голос в день, он может менять свой выбор по ходу дня, но в 18.00 по
        московскому времени голосование завершается.
    <p/>

    <p>Анонимным пользователям доступна только стартовая страница с возможностью зарегистрироваться.
        Кроме того, для тестирования приложения возможен вход с главной страницы под любой из трех выбранных ролей.</p>

    <p>Управление возможно через UI (по AJAX) и по REST интерфейсу с базовой авторизацией.<br/>
        Весь REST интерфейс покрывается JUnit тестами, используя Spring MVC Test и Spring Security Test.<br/>
        Реализованы базовая интернационализация, кэширование основных запросов, валидация вводимых пользователем данных
        и
        обработка ошибок.
    </p>

</div>

<jsp:include page="fragments/footer.jsp"/>

<script type="text/javascript">
    <c:if test="${not empty param.username}">
    setCredentials("${param.username}", "");
    </c:if>

    function login(username, password) {
        setCredentials(username, password);
        $("#login_form").submit();
    }

    function setCredentials(username, password) {
        $('input[name="username"]').val(username);
        $('input[name="password"]').val(password);
    }
</script>
</body>
</html>