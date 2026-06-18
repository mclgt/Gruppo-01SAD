# JavaFX Music Player
**Progetto di Software Architecture Design -A.A. 2025/2026**
## Overview del progetto
Sviluppo di un gestore di Playlist Musicali con riproduzione simulata nell'ambito del corso di Software Architecture Design a.a. 2025/2026. 

**Struttura della Repository e Documentazione**:

Il progetto è stato sviluppato seguendo la metodologia SCRUM, al suo interno, oltre al codice sorgente,  sono presenti diverse directory: 
- Nella directory " /Delivery" sono presenti i documenti di Sprint Review (Sprint Restrospective, Sprint Review, Burndown Chart aggiornato dopo ogni Sprint.
- Nella sotto-directory "/Delivery/First Delivery" è presente il documento architetturale e il link alla bacheca Trello utilizzata per sviluppare il progetto
- Nella sotto-directory "/Delivery/Last Delivery/Diagrammi delle classi" sono presenti i diagrammi delle classi suddivisi per pattern e aggiornati secondo la struttura finale del progetto
- Nella sotto-directory "/Delivery/Last Delivery" è presente la presentazione finale del progetto, in cui vengono illustrati i vari pattern utilizzati.


**Funzionalità principali**: 
- Riproduzione Audio con modalità personalizzate (Sequenziale,Shuffle, Loop per singolo brano e Loop per l'intera playlist).
- Possibilità di Aggiunta, Modifica e Rimozione singolo brano
- Possibilità di creare playlist manuale o generarle in modo automatico tramite filtri dinamici (Anno, Genere o Tag specifici)
- Sistema di Undo/Redo: ogni azione può essere annullata e ripristinata
- Tracciamento del numero di riproduzioni per singolo brano e per le playlist più ascoltate


## Vincoli e Formati di File Supportati
L'applicazione utilizza le librerie standard di Java per riprodurre in maniera simuata l'audio. Per garantire il corretto funzionamento, l'applicazione accetta file audio locali nel formato WAV (.wav).

I file devono risiedere sul file system della macchina su cui è in esecuzione l'applicazione. Percorsi di rete non validi o file non adatti verranno opportunamente segnalati. 
### Come avviare l'applicazione
Il progetto è gestito tramite **Maven**. Per compilare ed eseguire l'applicazione è sufficiente utilizzare il terminale. 

**Prerequisti**: 

- JDK 11 o superiore
- Apache Maven opportunamente configurato

**Istruzioni per l'avvio**

- Aprire il terminale, navigando fino alla directory principale del progetto
- Eseguire il comando:
```
mvn javafx:run
```



## Componenti del gruppo 
- Salvatore Addeo 
- Rebecca Beatrice
- Christian De Cesare
- Michela Gaeta
