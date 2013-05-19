<%@ include file="/base.jsp" %> 
<%@ taglib uri="/tags/tags" prefix="tags"%>

<ul id="usuario" class="dropdown-menu">
	<li><a href="javascript:gerarLinkCompleto('<c:url value="/usuario/editarUsuario"/>')">Editar</a></li>
	<li><a href="javascript:deletar('<c:url value="/usuario/excluirUsuario"/>')">Excluir</a></li>
	<li><a href="javascript:gerarLinkCompleto('<c:url value="/usuario/trocarSenha"/>')">Trocar a senha</a></li>
</ul>

<a class="btn" href="<c:url value="/usuario/criarUsuario"/>" > Criar usuario </a>

<br><br>

<form class="well form-inline" action="<c:url value="/usuario/listarUsuarios"/>" method="post" >
	
	<select name="usuario.postoGraduacao" >
		<option value="" style='display:none;' >Posto/Graduacao</option>
		<option value="" > Todos </option>
		<c:forEach items="${graduacoes}" var="item">
			<option <c:if test="${sessaoGeral.valor.get('usuario').postoGraduacao == item}"> selected="selected" </c:if> value="${item}"> ${item} </option>
		</c:forEach>
	</select>
	<input type="text" class="input-small" name="usuario.nome" value="${sessaoGeral.valor.get('usuario').nome}" placeholder="Nome">
	<input type="text" class="input-small" name="usuario.identidade" value="${sessaoGeral.valor.get('usuario').identidade}" placeholder="Identidade">
	<input type="text" class="input-small" name="usuario.subUnidade" value="${sessaoGeral.valor.get('usuario').subUnidade}" placeholder="Subunidade">
	<input type="text" class="input-medium" name="usuario.grupoUsuario.nome" value="${sessaoGeral.valor.get('usuario').grupoUsuario.nome}" placeholder="Grupo de Usuarios">
			
	<button type="submit" class="btn btn-info">Pesquisar</button>
</form>

<h3> Usuarios </h3>

<c:set var="link" value="usuario/listarUsuarios" scope="request" />
<%@ include file="/paginacao.jsp" %> 

<table class="table table-striped table-bordered">
  <thead>
    <tr>
      <th>Login</th>
      <th>Nome</th>
      <th>Empresa</th>
      <th>Administrador</th>
    </tr>
  </thead>
  <tbody>
  	<c:forEach items="${usuarios}" var="item">
		<tr id="usuario_${item.id}">
		  <td> ${item.login} </td>
		  <td> ${item.nome} </td>
		  <td> ${item.empresa.nome} </td>
		  <td> <tags:simNao valor="${item.administrador}" />  </td>
		</tr>
  	</c:forEach>  	
  </tbody>
</table>
