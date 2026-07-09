# Definición de Hecho (Definition of Done)

Un cambio está **hecho** cuando cumple todo esto. Es el checklist del Pull Request.

- [ ] **Funciona:** `mvn test` en verde (unitarias + BDD).
- [ ] **Probado:** el comportamiento nuevo o cambiado tiene pruebas que lo cubren.
- [ ] **Trazable:** si agregó/cambió una regla, se actualizó `traceability.md`.
- [ ] **Documentado:** README y docs afectados están actualizados; si hubo una decisión con
      alternativas, hay un ADR.
- [ ] **Seguro:** no versiona secretos, tokens reales ni PII; la configuración sensible va por env.
- [ ] **Reproducible:** si aplica, hay evidencia en `evidence/` y se puede regenerar con comandos.
- [ ] **Limpio:** commits pequeños y semánticos; se preservaron cambios ajenos.
- [ ] **Honesto:** no se afirma nada que no se haya ejecutado y mostrado.

## Por qué

Estos criterios son los que hacen que el trabajo sea **reutilizable, escalable y explicable** — no
solo "que ande en mi máquina".
