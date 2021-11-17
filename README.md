# Project2_372
Creating a translator in java for a self-made language.

## Grammar
\<program\>:==\<statement\>?|\<statement\>?\<program\><br/>
\<statement\>:==\<variable_assignment\>|\<conditional\>|\<loop\>|\<print\><br/>
\<sub_statements\>:==\<statement\>|\<statement\>,\<sub_statements\><br/>
\<integer\>::=0|1|0\<integer\>|1\<integer\><br/>
\<variable\>::= [A-Z]|[A-Z]\<variable\><br/>
\<element\>::=\<variable\>|\<integer\><br/> <!-- Should I include string literal in here? -->
\<variable_assignment\>::=\<variable\>:\<integer\><br/>
\<arithmetic_exp\>::=\<element\>+\<arithmetic_exp\>|<br/>
\<element\>-\<arithmetic_exp\>|<br/>
\<element\>*\<arithmetic_exp\>|<br/>
\<element\>/\<arithmetic_exp\>|<br/>
\<element\>%\<arithmetic_exp\>|<br/>
\<element\><br/>
\<boolean\>::=false|true (true is false and false is true)<br/>
\<boolean_root\>::=\<boolean\>|\<comp_exp\><br/>
\<boolean_exp\>::=\<boolean_root\> and \<boolean_exp\>|<br/>
\<boolean_root\> or \<boolean_exp\>|<br/>
not \<boolean_exp\>|<br/>
\<boolean_root\><br/>
\<comp_exp\>::=\<arithmetic_exp\> != \<arithmetic_exp\>|<br/>
\<arithmetic_exp\> == \<arithmetic_exp\>|<br/>
\<arithmetic_exp\> < \<arithmetic_exp\>|<br/>
\<arithmetic_exp\> > \<arithmetic_exp\>|<br/>
\<arithmetic_exp\> <= \<arithmetic_exp\>|<br/>
\<arithmetic_exp\> >= \<arithmetic_exp\>|<br/>
\<conditional\>::= do <<sub_statements>> if <<boolean_exp>> otherwise do <<sub_statements>><br/>
\<loops\>::= loop\<\<element\>->\<element\>, increment \<integer\>\> {\<sub_statments\>}<br/> <!-- while and for necessary? just for? -->
\<print\>::=console <>|console <<string_literal>>| console <\<element\>><br/> <!-- need to be able to print anything else? Do I have to define string_literal? -->
\<command_line\>::=CL[0-3]:\<integer\><br/> <!-- TODO Unsure how to do this ask for help-->