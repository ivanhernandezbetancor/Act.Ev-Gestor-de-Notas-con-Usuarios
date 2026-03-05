## Act.Ev-Gestor-de-Notas-con-Usuarios

##  Objetivo

- Desarrollar una aplicación de escritorio que permita gestionar notas de forma visual y organizada. La nota final dependerádel nivel de producto alcanzado: habrá un bloque de mínimos y un bloque de producto completo con mejoras de calidad.

## Cómo ejecutar

- Abre el proyecto en VS Code
- Asegúrate de tener instalada la extensión "Extension Pack for Java"
- Abre el archivo Main.java
- Pulsa F5 o haz clic en "Run" sobre el método main


## Estructura del Proyecto

📦 src/
│
└── 📁 gestonotasconUsuario/
    │
    ├── 📁 app/
    │   └── 📄 Main.java                   [Punto de entrada]
    │
    ├── 📁 gui/
    │   ├── 📄 Ventana.java               [Ventana principal]
    │   ├── 📄 Login.java                 [Autenticación]
    │   └── 📄 Notas.java                 [Gestión de notas]
    │
    ├── 📁 model/
    │   ├── 📄 Usuario.java               [Modelo Usuario]
    │   └── 📄 Nota.java                  [Modelo Nota]
    │
    ├── 📁 security/
    │   └── 📄 SecurityUtil.java          [Encriptación SHA-256]
    │
    ├── 📁 service/
    │   ├── 📄 UsuarioService.java        [Lógica de usuarios]
    │   └── 📄 NotaService.java           [Lógica de notas]
    │
    └── 📁 utils/
        └── 📄 Validador.java             [Validaciones]



## Capturas


- (login/registro)



- gestión de notas



- Búsqueda



- Borrado






