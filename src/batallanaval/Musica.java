package batallanaval;

import javax.sound.midi.*;

/**
 * Clase que maneja toda la música y efectos de sonido del juego.
 * Usa MIDI puro de Java (sin librerías externas).
 *
 * EFECTOS DE DISPARO:
 *   sonarDisparoAgua()  → Silbido de bomba + chapoteo de agua
 *   sonarDisparoBarco() → Silbido de bomba + explosión metálica + eco
 *   sonarHundido()      → Explosión grande + derrumbe + chapoteo final
 */
public class Musica {

    private Sequencer sequencer;
    private boolean   musicaActiva;

    public Musica() {
        musicaActiva = true;
    }

    // ══════════════════════════════════════════════════════
    //   MÚSICA DE FONDO
    // ══════════════════════════════════════════════════════

    public void tocarMenuPrincipal() {
        if (!musicaActiva) return;
        detener();
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.setSequence(crearMelodiaMenu());
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
        } catch (Exception e) {
            System.out.println("  [Música no disponible en este sistema]");
        }
    }

    public void tocarBatalla() {
        if (!musicaActiva) return;
        detener();
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.setSequence(crearMelodiaBatalla());
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
        } catch (Exception e) {
            System.out.println("  [Música no disponible en este sistema]");
        }
    }

    public void tocarVictoria() {
        if (!musicaActiva) return;
        detener();
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.setSequence(crearMelodiaVictoria());
            sequencer.setLoopCount(0);
            sequencer.start();
        } catch (Exception e) {
            System.out.println("  [Música no disponible en este sistema]");
        }
    }

    public void tocarDerrota() {
        if (!musicaActiva) return;
        detener();
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.setSequence(crearMelodiaDerrota());
            sequencer.setLoopCount(0);
            sequencer.start();
        } catch (Exception e) {
            System.out.println("  [Música no disponible en este sistema]");
        }
    }

    // ══════════════════════════════════════════════════════
    //   EFECTOS DE DISPARO
    // ══════════════════════════════════════════════════════

    /**
     * DISPARO AL AGUA:
     *   1. Silbido descendente de la bomba (bomba cayendo)
     *   2. SPLASH: golpe grave + burbujeo suave
     */
    public void sonarDisparoAgua() {
        if (!musicaActiva) return;
        new Thread(() -> {
            try {
                Synthesizer synth = MidiSystem.getSynthesizer();
                synth.open();
                MidiChannel[] ch = synth.getChannels();

                // ── 1. Silbido descendente de la bomba ──────────────
                // Instrumento 79 = ocarina (silbido agudo)
                ch[1].programChange(79);
                int[] silbido = {84, 81, 78, 74, 70, 65, 60, 55, 50};
                for (int nota : silbido) {
                    ch[1].noteOn(nota, 90);
                    Thread.sleep(55);
                    ch[1].noteOff(nota);
                }

                // ── 2. SPLASH ────────────────────────────────────────
                // Bombo grave = impacto del agua
                ch[9].noteOn(36, 127);   // Bombo: THUD
                Thread.sleep(60);
                // Hi-hat abierto repetido = salpicadura
                ch[9].noteOn(46, 80);
                Thread.sleep(50);
                ch[9].noteOn(46, 60);
                Thread.sleep(50);
                ch[9].noteOn(46, 40);
                Thread.sleep(50);
                ch[9].noteOn(46, 20);
                Thread.sleep(200);
                ch[9].allNotesOff();

                synth.close();
            } catch (Exception e) { /* Sin sonido */ }
        }).start();
    }

    /**
     * DISPARO A UN BARCO:
     *   1. Silbido descendente de la bomba (igual que agua)
     *   2. BANG metálico fuerte: explosión + golpe de metal
     *   3. Eco: crujido del casco dañado
     */
    public void sonarDisparoBarco() {
        if (!musicaActiva) return;
        new Thread(() -> {
            try {
                Synthesizer synth = MidiSystem.getSynthesizer();
                synth.open();
                MidiChannel[] ch = synth.getChannels();

                // ── 1. Silbido descendente ───────────────────────────
                ch[1].programChange(79); // Ocarina
                int[] silbido = {84, 81, 78, 74, 70, 65, 60, 55, 50};
                for (int nota : silbido) {
                    ch[1].noteOn(nota, 95);
                    Thread.sleep(50);
                    ch[1].noteOff(nota);
                }

                // ── 2. IMPACTO en metal ──────────────────────────────
                // Bombo fortísimo
                ch[9].noteOn(36, 127);   // BOOM principal
                Thread.sleep(30);
                // Crash cymbal = metal resonando
                ch[9].noteOn(49, 127);   // Crash fuerte
                Thread.sleep(30);
                // Caja = golpe seco metálico
                ch[9].noteOn(38, 110);
                Thread.sleep(30);
                // Tom agudo = rebote del casco
                ch[9].noteOn(50, 90);
                Thread.sleep(80);

                // ── 3. ECO: crujido del barco ────────────────────────
                // Instrumento 122 = efecto de mar (crujido)
                ch[2].programChange(122);
                ch[2].noteOn(40, 70);
                Thread.sleep(120);
                ch[2].noteOn(38, 50);
                Thread.sleep(120);
                ch[2].noteOn(36, 30);
                Thread.sleep(300);

                ch[9].allNotesOff();
                ch[2].allNotesOff();
                synth.close();

            } catch (Exception e) { /* Sin sonido */ }
        }).start();
    }

    /**
     * BARCO HUNDIDO COMPLETAMENTE:
     *   1. Silbido final
     *   2. Explosión masiva en 3 capas
     *   3. Derrumbe del barco hundiéndose
     *   4. Burbujeo final al sumergirse
     */
    public void sonarHundido() {
        if (!musicaActiva) return;
        new Thread(() -> {
            try {
                Synthesizer synth = MidiSystem.getSynthesizer();
                synth.open();
                MidiChannel[] ch = synth.getChannels();

                // ── 1. Silbido rápido ────────────────────────────────
                ch[1].programChange(79);
                int[] silbido = {80, 72, 64, 55};
                for (int nota : silbido) {
                    ch[1].noteOn(nota, 100);
                    Thread.sleep(40);
                    ch[1].noteOff(nota);
                }

                // ── 2. EXPLOSIÓN MASIVA (3 golpes) ───────────────────
                // Capa 1: boom inicial
                ch[9].noteOn(36, 127);
                ch[9].noteOn(49, 127);
                Thread.sleep(80);

                // Capa 2: segunda onda expansiva
                ch[9].noteOn(35, 127);
                ch[9].noteOn(57, 110);
                Thread.sleep(80);

                // Capa 3: reverberación
                ch[9].noteOn(41, 100);
                ch[9].noteOn(46, 90);
                Thread.sleep(100);

                // ── 3. DERRUMBE del barco ────────────────────────────
                // Notas graves descendentes = barco cayendo
                ch[3].programChange(116); // Taiko drum (bajo)
                int[] caida = {48, 45, 43, 40, 38, 36};
                for (int nota : caida) {
                    ch[3].noteOn(nota, 100);
                    Thread.sleep(80);
                    ch[3].noteOff(nota);
                }

                // ── 4. BURBUJEO final ────────────────────────────────
                // Hi-hats suaves y decrecientes = barco sumergiéndose
                int[] volBurbujeo = {90, 70, 55, 40, 25, 15};
                for (int vol : volBurbujeo) {
                    ch[9].noteOn(42, vol);
                    Thread.sleep(90);
                }

                Thread.sleep(300);
                ch[9].allNotesOff();
                ch[3].allNotesOff();
                ch[1].allNotesOff();
                synth.close();

            } catch (Exception e) { /* Sin sonido */ }
        }).start();
    }

    // ══════════════════════════════════════════════════════
    //   CONTROL
    // ══════════════════════════════════════════════════════

    public void detener() {
        try {
            if (sequencer != null && sequencer.isRunning()) {
                sequencer.stop();
                sequencer.close();
            }
        } catch (Exception e) { /* ignorar */ }
    }

    public void toggleMusica() {
        musicaActiva = !musicaActiva;
        if (!musicaActiva) detener();
    }

    public boolean isMusicaActiva() { return musicaActiva; }

    // ══════════════════════════════════════════════════════
    //   MELODÍAS MIDI
    // ══════════════════════════════════════════════════════

    private Sequence crearMelodiaMenu() throws InvalidMidiDataException {
        Sequence seq = new Sequence(Sequence.PPQ, 8);
        Track track = seq.createTrack();
        ShortMessage inst = new ShortMessage();
        inst.setMessage(ShortMessage.PROGRAM_CHANGE, 0, 56, 0);
        track.add(new MidiEvent(inst, 0));
        int[][] notas = {
            {60,0,4},{60,4,4},{60,8,4},{55,12,6},
            {57,18,2},{60,20,4},{62,24,4},{64,28,8},
            {64,36,4},{62,40,4},{60,44,4},{57,48,6},
            {55,54,2},{57,56,4},{60,60,12}
        };
        agregarNotas(track, notas, 100);
        return seq;
    }

    private Sequence crearMelodiaBatalla() throws InvalidMidiDataException {
        Sequence seq = new Sequence(Sequence.PPQ, 6);
        Track track = seq.createTrack();
        ShortMessage inst = new ShortMessage();
        inst.setMessage(ShortMessage.PROGRAM_CHANGE, 0, 58, 0);
        track.add(new MidiEvent(inst, 0));
        Track drums = seq.createTrack();
        int[][] perc = {
            {35,0,2},{38,6,2},{35,12,2},{38,18,2},
            {35,24,2},{38,30,2},{35,36,2},{38,42,2}
        };
        agregarNotasCanal(drums, perc, 9, 110);
        int[][] notas = {
            {48,0,3},{50,3,3},{52,6,3},{53,9,3},
            {55,12,6},{53,18,3},{52,21,3},{50,24,6},
            {48,30,3},{47,33,3},{48,36,12}
        };
        agregarNotas(track, notas, 110);
        return seq;
    }

    private Sequence crearMelodiaVictoria() throws InvalidMidiDataException {
        Sequence seq = new Sequence(Sequence.PPQ, 8);
        Track track = seq.createTrack();
        ShortMessage inst = new ShortMessage();
        inst.setMessage(ShortMessage.PROGRAM_CHANGE, 0, 56, 0);
        track.add(new MidiEvent(inst, 0));
        int[][] notas = {
            {60,0,2},{64,2,2},{67,4,2},{72,6,8},
            {71,14,2},{69,16,2},{67,18,2},{65,20,2},
            {64,22,2},{65,24,4},{67,28,4},{69,32,8},
            {67,40,2},{65,42,2},{64,44,4},{60,48,12}
        };
        agregarNotas(track, notas, 120);
        return seq;
    }

    private Sequence crearMelodiaDerrota() throws InvalidMidiDataException {
        Sequence seq = new Sequence(Sequence.PPQ, 6);
        Track track = seq.createTrack();
        ShortMessage inst = new ShortMessage();
        inst.setMessage(ShortMessage.PROGRAM_CHANGE, 0, 70, 0);
        track.add(new MidiEvent(inst, 0));
        int[][] notas = {
            {67,0,6},{65,6,6},{64,12,6},{62,18,6},{60,24,12}
        };
        agregarNotas(track, notas, 80);
        return seq;
    }

    private void agregarNotas(Track track, int[][] notas, int vel)
            throws InvalidMidiDataException {
        agregarNotasCanal(track, notas, 0, vel);
    }

    private void agregarNotasCanal(Track track, int[][] notas, int canal, int vel)
            throws InvalidMidiDataException {
        for (int[] n : notas) {
            ShortMessage on = new ShortMessage();
            on.setMessage(ShortMessage.NOTE_ON, canal, n[0], vel);
            track.add(new MidiEvent(on, n[1]));
            ShortMessage off = new ShortMessage();
            off.setMessage(ShortMessage.NOTE_OFF, canal, n[0], 0);
            track.add(new MidiEvent(off, n[1] + n[2]));
        }
    }
}