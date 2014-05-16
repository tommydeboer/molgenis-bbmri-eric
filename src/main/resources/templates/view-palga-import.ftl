<#include "molgenis-header.ftl">
<#include "molgenis-footer.ftl">

<#assign css=[] />
<#assign js=[] />

<@header css js/>

<#if showThesurus>
<form class="form-horizontal"  method="POST" action="${context_url}/thesaurus">
	<div class="control-group">
    	<label class="control-label">Thesaurus file location:</label>
    	<div class="controls">
    		<input type="text" name="fileLocation" class="required ">
    		<button type="submit" class="btn">Import thesaurus file</button>
    		(Can only be imported once)
		</div>
	</div>
</form>
</#if>

<form class="form-horizontal"  method="POST" action="${context_url}/palgasample">
	<div class="control-group">
    	<label class="control-label">Palga sample file location:</label>
    	<div class="controls">
    		<input type="text" name="fileLocation" class="required ">
    		<button type="submit" class="btn">Import palga sample file</button>
    		(Adds new samples)
		</div>
	</div>
</form>

<form class="form-horizontal"  method="POST" action="${context_url}/retrievalterms">
	<div class="control-group">
    	<label class="control-label">Retrieval items file location:</label>
    	<div class="controls">
    		<input type="text" name="fileLocation" class="required ">
    		<button type="submit" class="btn">Import retrieval items file</button>
    		(Adds or updates items)
		</div>
	</div>
</form>

<@footer/>