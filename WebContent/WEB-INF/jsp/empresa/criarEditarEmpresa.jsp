<%@ include file="/base.jsp" %> 

<form class="form-horizontal" action="<c:url value="/empresa/salvarEmpresa"/>" method="post">
  <fieldset>
    <legend>Criar/editar empresa</legend>
    <div class="control-group">
      <label class="control-label">Nome</label>
      <div class="controls">
        <input type="text" class="input-xlarge" name="empresa.nome" value="${empresa.nome}">
      </div>
    </div>

    <button type="submit" class="btn btn-primary">Salvar</button>
    <a class="btn btn-danger" href="<c:url value="/empresa/listarEmpresas"/>" > Cancelar </a>
  </fieldset>
</form>
