<%@ include file="/base.jsp" %> 
<%@ taglib uri="/tags/tags" prefix="tags"%>

<ul id="empresa" class="dropdown-menu">
	<li><a href="javascript:gerarLinkCompleto('<c:url value="/empresa/editarEmpresa"/>')">Editar</a></li>
	<li><a href="javascript:deletar('<c:url value="/empresa/excluirEmpresa"/>')">Excluir</a></li>
</ul>

<a class="btn" href="<c:url value="/empresa/criarEmpresa"/>" > Criar empresa </a>

<br><br>

<form class="well form-inline" action="<c:url value="/empresa/listarEmpresas"/>" method="post" >
    <input type="text" class="input-small" name="empresa.nome" value="${sessaoGeral.valor.get('empresa').nome}" placeholder="Nome">

	<button type="submit" class="btn btn-info">Pesquisar</button>
</form>

<h3> Empresas </h3>

<c:choose>
	<c:when test="${!empty empresas}">
		
		<c:set var="link" value="empresa/listarEmpresas" scope="request" />
		<%@ include file="/paginacao.jsp" %> 
		
		<table class="table table-striped table-bordered tablesorter">
			<thead>
		    	<tr>
                    <th> Nome </th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${empresas}" var="item">
					<tr id="empresa_${item.id}">
                        <td> ${item.nome} </td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:when>
	<c:otherwise>
		<br>  <br>  <h4> Nenhum registro foi encontrado </h4>
	</c:otherwise>
</c:choose>
