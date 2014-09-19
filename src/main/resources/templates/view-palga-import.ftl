<#include "molgenis-header.ftl">
<#include "molgenis-footer.ftl">

<#assign css=[] />
<#assign js=[] />

<@header css js/>
<div class="row">
    <div class="col-md-offset-2 col-md-8">
        <form class="form-horizontal"  method="POST" action="${context_url}/palgasample" role="form">
            <div class="form-group">
            	<label for="exampleInputPassword1" class="col-md-3 control-label">PALGA sample file location:</label>
            	<div class="col-md-4">
        		  <input type="text" name="fileLocation" class="form-control" required>
        		</div>
        	</div>
        	<div class="form-group">
        	   <div class="col-md-offset-3 col-md-8">
        	       <button type="submit" class="btn btn-default">Import PALGA sample file</button>
        	   </div>
        	</div>
        </form>
    </div>
</div>
<@footer/>