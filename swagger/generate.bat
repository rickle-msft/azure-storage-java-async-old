set version=2.0.7
set url=https://github.com/Azure/autorest.java/releases/download/v%version%/microsoft.azure-autorest.java-%version%.tgz
autorest %~dp0README.md --use=%url% --reset --preview