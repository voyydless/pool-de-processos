package processos;

/* Representa um processo que imprime na tela o estado atual da fila de processos,
 seu objetivo sendo visualizar os processos presentes no pool. */
public class PrintingProcess extends Process {
    private ProcessPool processPool; // Referência ao pool principal para exibir a fila

    // Construtor
    public PrintingProcess(ProcessPool processPool) {
        super(); // Chama o construtor da superclasse Process para gerar um PID
        this.processPool = processPool;
    }

    // Construtor para recriar um processo de impressão a partir de um arquivo,
    // restaurando o PID e a referência ao pool de processos
    public PrintingProcess(ProcessPool processPool, int pid) {
        super(pid); // Restaura o PID
        this.processPool = processPool;
    }

    // Executa a lógica do processo de impressão,
    // chamando o método do ProcessPool para exibir a fila de processos
    @Override
    public void execute() {
        System.out.println("Executando Processo de Impressão (PID: " + pid + "): Imprimindo o estado atual do pool de processos...");
        processPool.mostrarFila(); // Chama o método do pool para exibir sua fila
        System.out.println("Processo de Impressão (PID: " + pid + ") concluído.");
    }

    // Retorna uma representação em string deste processo, incluindo seu PID
    @Override
    public String toString() {
        return "Processo de Impressão (PID: " + pid + ")";
    }

    // Retorna uma string formatada contendo o tipo do processo e o PID.
    @Override
    public String toFileString() {
        // Salva apenas o tipo e o PID, pois não possui atributos adicionais.
        return getType() + "|" + pid;
    }

    // Getter
    // Retorna o tipo deste processo
    @Override
    public String getType() {
        return "PrintingProcess";
    }
}