package processos;

/* Representa um processo genérico no sistema, sendo a superclasse para todos os tipos
específicos de processos. Todo processo possui um ID único (PID) e um método para sua execução. */
public abstract class Process {
    protected int pid; // Identificador único do processo
    private static int nextPid = 1; // Próximo PID disponível para novos processos

    // Construtor padrão para criar um novo processo
    // Gera automaticamente um PID único para o processo
    public Process() {
        this.pid = nextPid++;
    }

    public Process(int pid) {
        this.pid = pid;
        // Garante que o próximo PID gerado seja maior que qualquer PID restaurado
        if (pid >= nextPid) {
            nextPid = pid + 1;
        }
    }

    // Método abstrato de execução para ser implementada e especificada em todas as subclasses
    public abstract void execute();

    // Retorna uma string formatada contendo todas as informações necessárias para salvar o estado deste processo em um arquivo,
    // permitindo que o processo seja recriado depois
    public abstract String toFileString();

    // Retorna uma representação em string do processo para exibir ao usuário
    @Override
    public String toString() {
        return "Processo (PID: " + pid + ")";
    }

    // Define o valor do próximo PID a ser utilizado.
    public static void setNextPid(int newNextPid) {
        nextPid = newNextPid;
    }

    // Getters
    public int getPid() { return pid; }
    // Retorna o tipo específico do processo como uma string
    public abstract String getType();

}