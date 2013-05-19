<%@ include file="/base.jsp" %> 

<form class="form-horizontal" action="<c:url value="/usuario/salvarUsuario"/>" method="post">
  <fieldset>
    <legend>Criar/editar usuario</legend>
    
    <div class="control-group warning">
      <label class="control-label">Nome</label>
      <div class="controls">
        <input type="text" class="input-xlarge required" name="usuario.nome" value="${usuario.nome}">
      </div>
    </div>
    
    <div class="control-group warning">
      <label class="control-label">Login</label>
      <div class="controls">
        <input type="text" class="input-xlarge required" name="usuario.login" value="${usuario.login}">
      </div>
    </div>
    
    <c:if test="${empty sessaoGeral.valor.get('idUsuario')}">
	    <div class="control-group warning">
	    	<label class="control-label">Senha</label>
	    	<div class="controls">
	    		<input type="password" class="input-xlarge required"  name="usuario.senha" value="" >
			</div>
		</div>
    </c:if>
    
	<div class="control-group warning">
		<label class="control-label">Empresa</label>
			<div class="controls">
	          <select name="usuario.empresa.id" >
				<c:forEach items="${empresas}" var="item">
					<option <c:if test="${usuario.empresa.id == item.id}"> selected="selected" </c:if> value="${item.id}"> ${item.nome} </option>
				</c:forEach>
			  </select>
        </div>
    </div>
    
    
    <div class="control-group warning">
        <label class="control-label">Administrador</label>
        <div class="controls">
          <select name="usuario.administrador" >
			<option <c:if test="${usuario.administrador == true}"> selected="selected" </c:if> value="true"> Sim </option>
			<option <c:if test="${usuario.administrador == false}"> selected="selected" </c:if> value="false"> Não </option>
		  </select>
        </div>
    </div>

    <button type="submit" class="btn btn-primary">Salvar</button>
    <a class="btn btn-danger" href="<c:url value="/usuario/listarUsuarios"/>" > Cancelar </a>
  </fieldset>
</form>