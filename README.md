# Project2_372
Creating a translator in java for a self-made language.

## Grammar
\<program\>:==\<statement\>?|\<statement\>?\<program\><br/><br/>
\<statement\>:==\<variable_assignment\>|\<conditional\>|\<loop\>|\<print\><br/><br/>
\<sub_statements\>:==\<statement\>|\<statement\>,\<sub_statements\><br/><br/>
\<integer\>::=0|1|0\<integer\>|1\<integer\><br/><br/>
\<variable\>::= [A-Z]|[A-Z]\<variable\><br/><br/>
\<element\>::=\<variable\>|\<integer\><br/><br/> <!-- Should I include string literal in here? -->
\<variable_assignment\>::=\<variable\>:\<integer\><br/><br/>
\<arithmetic_exp\>::=\<element\>+\<arithmetic_exp\>|<br/>
\<element\>-\<arithmetic_exp\>|<br/>
\<element\>*\<arithmetic_exp\>|<br/>
\<element\>/\<arithmetic_exp\>|<br/>
\<element\>%\<arithmetic_exp\>|<br/>
\<element\><br/><br/>
\<boolean\>::=false|true (true is false and false is true)<br/><br/>
\<boolean_root\>::=\<boolean\>|\<comp_exp\><br/><br/>
\<boolean_exp\>::=\<boolean_root\> and \<boolean_exp\>|<br/>
\<boolean_root\> or \<boolean_exp\>|<br/>
not \<boolean_exp\>|<br/>
\<boolean_root\><br/><br/>
\<comp_exp\>::=\<element\> != \<arithmetic_exp\>|<br/>
\<element\> == \<arithmetic_exp\>|<br/>
\<element\> < \<arithmetic_exp\>|<br/>
\<element\> > \<arithmetic_exp\>|<br/>
\<element\> <= \<arithmetic_exp\>|<br/>
\<element\> >= \<arithmetic_exp\>|<br/>
\<element\><br/><br/>
\<conditional\>::= do <<sub_statements>> if <<boolean_exp>> otherwise do <<sub_statements>><br/><br/>
\<loops\>::= loop\<\<element\>->\<element\>, increment \<integer\>\> {\<sub_statments\>}<br/><br/> <!-- while and for necessary? just for? -->
\<print\>::=console <>|console <<string_literal>>| console <\<element\>><br/><br/> <!-- need to be able to print anything else? Do I have to define string_literal? -->
\<command_line\>::=CL[0-3]:\<integer\><br/> <!-- TODO Unsure how to do this ask for help-->
\<string_literal\>::='\<string\>'
\<string\>::=[^?,] <!-- Any character except ? and , because of the parsing requirements -->