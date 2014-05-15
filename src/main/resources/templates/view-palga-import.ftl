<#include "molgenis-header.ftl">
<#include "molgenis-footer.ftl">

<#assign css=[] />
<#assign js=[] />

<@header css js/>

<form class="form-horizontal"  method="POST" action="${context_url}/thesaurus">
	<div class="control-group">
    	<label class="control-label">Thesaurus file location:</label>
    	<div class="controls">
    		<input type="text" name="fileLocation" class="required ">
    		<button type="submit" class="btn">Import thesaurus file</button>
		</div>
	</div>
</form>

<form class="form-horizontal"  method="POST" action="${context_url}/palgasample">
	<div class="control-group">
    	<label class="control-label">Palga sample file location:</label>
    	<div class="controls">
    		<input type="text" name="fileLocation" class="required ">
    		<button type="submit" class="btn">Import palga sample file</button>
		</div>
	</div>
</form>

<@footer/>