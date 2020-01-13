package br.fatec.tank;

public class ArenaSettings {
	protected static int arenaPort = 9989;
	protected static int alturaTela;
	protected static int larguraTela;
	public static int getAlturaTela() {
		return alturaTela;
	}
	public static void setAlturaTela(int alturaTela) {
		ArenaSettings.alturaTela = alturaTela;
	}
	public static int getLarguraTela() {
		return larguraTela;
	}
	public static void setLarguraTela(int larguraTela) {
		ArenaSettings.larguraTela = larguraTela;
	}
	public static int getArenaPort() {
		return arenaPort;
	}
	public static void setArenaPort(int arenaPort) {
		ArenaSettings.arenaPort = arenaPort;
	}
}
