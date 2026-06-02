this is going to be project generator for our kmp starter template, here's what it will do:

- create new project (cli + web)
- add into existing project (cli)

there will be two methods:
- libs based
- modules based


libs based:
- when in web or cli (create or adding into existing project) user select libs based then add starter modules as library

modules based:
- when in web or cli (create or adding into existing project) user select libs based then add starter modules as modules
- fetch project from github & allow user to add them


features:
- create project 
- see kmp starter available versions
- add into existing project
- user should be able to add all or one module
- if one module/lib added, for example starter/ui/layouts then all modules that layouts depends on should also be added (tell user about it as well)
- upgrade version for example if current version is 0.4.0 & user upgrade the module. then if user had made changes in that module (means added through module method not lib) then give user warning & ask for confirmation else upgrade that module
- allow to upgrade versions for 1 module or all modules. support both module & lib
- for tracking versions create starter.json in the root of project (ps: also add this into project when using web generator)


note:
adding into exisitng project only supported through cli not web generator, web generator only use for generating project


architecture:
- all logic should be written inside data & domain layer 
- cli & web only should be consumer for different interface so that we don't have duplicate logic
