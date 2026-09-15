# campo-minado-java
Projeto de Campo Minado desenvolvido em Java com Swing e arquitetura MVC, incluindo temas visuais, skins de bandeira, aviso de excesso de marcações e modo tela cheia.

# Campo Minado em Java

Projeto de Campo Minado desenvolvido em Java utilizando Swing e arquitetura MVC.

## Funcionalidades implementadas

- Tema Terminal Retrô
- Diferentes skins para marcação de bandeiras
- Aviso ao ultrapassar o número de minas
- Modo tela cheia

## Requisitos

- JDK 21 ou superior

## Como executar

Compile:

```powershell
$files = Get-ChildItem -Path src -Recurse -Filter *.java | Where-Object { $_.FullName -notmatch '\\test\\' } | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d bin $files
