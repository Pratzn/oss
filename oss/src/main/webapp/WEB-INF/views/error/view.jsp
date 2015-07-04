<%-- Copyright (C) 2015 Thai NS Solutions Co., Ltd. All Rights Reserved. --%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<spring:url var="urlTop" value="/" />

<p class="alingnC strong"><c:out value="${message}" /></p>
<a href="${urlTop}">トップページへ</a>
