caseid,description,<#list ParameterNames as pn>${pn?trim}<#if pn_has_next>,</#if></#list>,expect,message
<#assign n = 1 >
<#list paraMap?keys as key>
	<#if paraMap[key]=="String"> 
${n},${key}为空,<#list paraMap?keys as num><#if key_index==num_index>,<#else></#if><#if num_has_next>a,</#if></#list>false,${key}:${key}不能为空
<#assign n = n + 1>
${n},${key}为null,<#list paraMap?keys as num><#if key_index==num_index>null,<#else></#if><#if num_has_next>a,</#if></#list>false,${key}:${key}不能为空
<#assign n = n + 1>
	</#if> 
</#list>