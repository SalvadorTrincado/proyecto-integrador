Este es el proyecto del grupo C, compuesto por Salvador Trincado, Andriy Borukh, Miguel Parra y Jose David Cabeza.
Esta divido en diferentes ramas, cada rama tiene el nombre de cada uno, se ha trabajado por separado:
Front End (Vistas, controladores, DTOs, JS y CSS) - Salva
Back End y BBDD. - Andriy y Salva
Seguridad y API REST - Miguel y Jose David

Se usaron principalmente la rama main, rama miguel, rama josedavid y rama andriy.

A fecha 29/04/2025, esta todo hecho, pero falta la buena configuracion y adaptacion entre ramas.
--------------------------------------------------------------------------------------------------------------------------------------
Proyecto Integrador - Requisitos Básicos
1. Autor(es):
Salvador Trincado, Andriy Borukh, Miguel, Jose David
2. URL del repositorio (privado):
https://github.com/SalvadorTrincado/proyecto-integrador
Commit: b03a12f67e1e9cd0a24d16e0f9ce435bca3d51ae
3. Tiempo invertido:
Aproximadamente 160 horas:
- Salvador: 60 h
- Andriy: 50 h
- Miguel: 25 h
- Jose David: 25 h
4. IDE utilizado:
IntelliJ IDEA Ultimate
5. Navegadores probados:
Google Chrome, Mozilla Firefox
6. Ruta de inicio:
http://localhost:8081/registro/empleado/paso1
7. Tabla de Métodos (Controladores):
- LoginPaso1Controller: GET /login/empleado/paso1 (Email)
- LoginPaso2Controller: GET /login/empleado/paso2 (Pregunta secreta)
- LoginPaso3Controller: GET /login/empleado/paso3 (Contraseña)
- RegistroEmpleadoPaso1Controller: GET /registro/empleado/paso1 (Datos personales)
- RegistroEmpleadoPaso2Controller: GET /registro/empleado/paso2 (Datos contacto)
- RegistroEmpleadoPaso3Controller: GET /registro/empleado/paso3 (Especialidades)
- RegistroEmpleadoPaso4Controller: GET /registro/empleado/paso4 (Datos bancarios)
Proyecto Integrador - Requisitos Básicos
- RegistroEmpleadoResumenController: GET /registro/empleado/resumen (Resumen)
- RegistroEmpleadoResumenController: POST /registro/empleado/resumen (Guardar)
- EtiquetadoController: GET y POST /empleados/etiquetado (Etiquetas subordinados)

8. Instalación necesaria:
- Tener instalado IntelliJ IDEA.
- Configurar Gradle y base de datos H2 persistente.
- Ejecutar ./gradlew bootRun.
- Acceso: http://localhost:8081/
9. Explicaciones complementarias:
Proyecto dividido en:
- app-empleados: Vistas, controladores, DTOs, JS, CSS.
- app-seguridad: Seguridad, API REST, control accesos.
- comun: Entidades y carga de datos inicial.
