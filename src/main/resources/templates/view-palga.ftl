<#include "molgenis-header.ftl">
<#include "molgenis-footer.ftl">

<#assign css=[] />
<#assign js=[] />

<@header css js/>

<div class="row" id="delete-entities" >
	<div class="col-md-3">
		<ul class="list-group">
			<li class="list-group-item"><a href="${context_url}/delete/PalgaSample">Delete Samples</a></li>
			<li class="list-group-item"><a href="${context_url}/delete/DRTPWRK">Delete Diagnosis</a></li>
			<li class="list-group-item"><a href="${context_url}/delete/Retrievalterm">Delete Retrievalterms</a></li></ul>
		</ul>
	</div>
</div>

<script>
	$('#delete-entities a').on('click', function(e) {
		if (!confirm('Delete all?')) {
			e.preventDefault();
			e.stopPropagation();
		} 
	});
</script>

<@footer/>