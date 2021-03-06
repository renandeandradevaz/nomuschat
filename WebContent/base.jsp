<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE HTML>
<html lang="pt-br">
	<head>
		<title> Nomus Chat </title>
		<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />
		<link type="text/css" href="<c:url value="/css/bootstrap.css"/>" rel="stylesheet" />
		<link type="text/css" href="<c:url value="/css/bootstrap-alterado.css"/>" rel="stylesheet" />
		<link type="text/css" href="<c:url value="/css/bootstrap-responsive.css"/>" rel="stylesheet" />
		<link type="text/css" href="<c:url value="/css/estilo.css"/>" rel="stylesheet" />
		<link type="text/css" href="<c:url value="/css/menu.css"/>" rel="stylesheet" />
		<link type="text/css" href="<c:url value="/css/jquery-ui-1.9.2.custom.min.css"/>" rel="stylesheet" />
		<link type="text/css" href="<c:url value="/css/jquery.ui.chatbox.css"/>" rel="stylesheet" />
		<script type="text/javascript" src="<c:url value="/js/jquery-1.8.3.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/js/jquery-ui-1.9.2.custom.min.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/js/jquery.ui.chatbox.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/js/chatboxManager.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/js/jquery.validate.js"/>"></script>
		<script type="text/javascript" charset="utf-8" src="<c:url value="/js/scripts.gerais.js"/>"></script>			
		<script type="text/javascript" src="<c:url value="/js/menu.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/js/submenu.js"/>"></script>
		<script type="text/javascript" src="<c:url value="/js/form.requerido.js"/>"></script>		
	</head>
	
	<noscript>
		<meta http-equiv="Refresh" content="0;url=<c:url value="/javascriptDesabilitado.jsp"/>">
	</noscript>

	 <body data-spy="scroll" data-target=".subnav" data-offset="50" style="position: relative;">
	 
	 	<div id="mostrar-menu" class="esconder-mostrar-menu">
	 		<p> Exibir </p> 
	 	</div>

		<div id="fixed-top" class="navbar navbar-inverse navbar-fixed-top" >
			<div style="background: black; box-shadow: 5px 5px 10px grey;" >
				<div id="menu">
				    <ul class="menu">
				        <li>
				        	<a href="" class="parent"><span>Menu</span></a>
				            <ul>
				            	<li><a href="<c:url value="/chat/chat"/>"><span> Chat </span></a></li>
				            	<c:if test="${sessaoUsuario.usuario.administrador}">
					            	<li><a href="<c:url value="/usuario/listarUsuarios"/>"><span> Usuários </span></a></li>
					            	<li><a href="<c:url value="/empresa/listarEmpresas"/>"><span> Empresas </span></a></li>
					            	<li><a href="<c:url value="/configuracao/configuracoes"/>"><span> Configurações </span></a></li>
				            	</c:if>
				            </ul>
				        </li>     
				    </ul>
				    <ul class="menu-usuario" >
				    	<li><span> Logado como: ${sessaoUsuario.usuario.login} &nbsp; </span> </li>
				    </ul>
				</div>
			</div>
			
			<a id="sair" style="float: right; padding-right: 15px; font-weight: bold; margin-top: 5px; font-size: 12px; cursor: pointer; " href="<c:url value="/login/logout"/>"> Sair </a>
			<a style="float: right; padding-right: 15px; font-weight: bold; margin-top: 5px; font-size: 12px; cursor: pointer; " href="<c:url value="/login/trocarPropriaSenha"/>"> Trocar senha </a>
		</div>		
		
		<a style="display: none;" href="http://apycom.com/"></a>
		
		<div id="divconteudo" style="margin-left: 30px; margin-right: 30px; margin-bottom: 100px; margin-top: 75px;" >
		
		<c:if test="${not empty sucesso}">
			<div class="alert alert-success">
				${sucesso}
			</div>
		</c:if>
		
		<c:if test="${not empty errors}">
			<div class="alert alert-error">
				<c:forEach items="${errors }" var="error">
					<strong>${error.category }</strong> - ${error.message } <br>
				</c:forEach>
			</div>
		</c:if>
		
 		<br>