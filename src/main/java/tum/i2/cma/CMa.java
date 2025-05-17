package tum.i2.cma;

import tum.i2.common.VirtualMachine;

public class CMa implements VirtualMachine {

    final int MEM_SIZE = 2 << 22;     // 1 MB stack

    private CMaInstruction[] instructions;
    private int PC, SP, FP, EP, NP;
    private int[] S;

    public CMa(CMaInstruction[] instructions) {
        this.instructions = instructions;
        this.PC = 0;
        this.SP = -1;
        this.FP = -1;
        this.EP = -1;
        this.S = new int[MEM_SIZE];
        this.NP = MEM_SIZE;         // heap begins at end of mem space
    }

    @Override
    public void step() {
        // TODO Implement one execution step

    }

    @Override
    public int run() {
        // We have defined the step() method here,
        // because it might make it easier to debug,
        // and would be required if you wish to implement
        // an interface with step function

        while(true) {
            CMaInstruction insn = instructions[PC];
            PC++;
            execute(insn);

            if(insn.getType() == CMaInstructionType.HALT)
                break;
        }

        return S[0];
    }

    public void execute(CMaInstruction instruction) {
        // CMaInstructionType enum contains comments,
        // describing where the operations are defined
        switch (instruction.getType()) {
            case LOADC:
                SP++;
                S[SP] = instruction.getFirstArg();
                break;
            case ADD:
                S[SP - 1] += S[SP];
                SP--;
                break;
            case SUB:
                S[SP - 1] -= S[SP];
                SP--;
                break;
            case MUL:
                S[SP - 1] *= S[SP];
                SP--;
                break;
            case DIV:
                S[SP - 1] /= S[SP];
                SP--;
                break;
            case MOD:
                S[SP - 1] %= S[SP];
                SP--;
                break;
            case AND:
                S[SP - 1] &= S[SP];
                SP--;
                break;
            case OR:
                S[SP - 1] |= S[SP];
                SP--;
                break;
            case XOR:
                S[SP - 1] ^= S[SP];
                SP--;
                break;
            case EQ:
                S[SP - 1] = S[SP - 1] == S[SP] ? 1 : 0;
                SP--;
                break;
            case NEQ:
                S[SP - 1] = S[SP - 1] != S[SP] ? 1 : 0;
                SP--;
                break;
            case LE:
                S[SP - 1] = S[SP - 1] < S[SP] ? 1 : 0;
                SP--;
                break;
            case LEQ:
                S[SP - 1] = S[SP - 1] <= S[SP] ? 1 : 0;
                SP--;
                break;
            case GR:
                S[SP - 1] = S[SP - 1] > S[SP] ? 1 : 0;
                SP--;
                break;
            case GEQ:
                S[SP - 1] = S[SP - 1] >= S[SP] ? 1 : 0;
                SP--;
                break;
            case NOT:
                S[SP] = S[SP] != 0 ? 0 : 1;
                break;
            case NEG:
                S[SP] = -S[SP];
                break;
            case LOAD: {
                for(int i = 0; i < instruction.getFirstArg(); i++) {
                    S[SP + i] = S[S[SP] + i];
                }
                break;
            }
            case STORE: {
                for(int i = 0; i < instruction.getFirstArg(); i++) {
                    S[S[SP] + i] = S[SP - instruction.getFirstArg() + i];
                }
                SP--;
                break;
            }
            case LOADA:
                SP++;
                S[SP] = S[instruction.getFirstArg()];
                break;
            case STOREA:
                S[instruction.getFirstArg()] = S[SP];
                break;
            case POP:
                SP--;
                break;
            case JUMP:
                PC = instruction.getFirstArg();
                break;
            case JUMPZ:
                if(S[SP] == 0) {
                    PC = instruction.getFirstArg();
                }
                SP--;
                break;
            case JUMPI:
                PC = instruction.getFirstArg() + S[SP];
                SP--;
                break;
            case DUP:
                SP++;
                S[SP] = S[SP - 1];
                break;
            case ALLOC:
                SP += instruction.getFirstArg();
                break;
            case NEW:
                if (NP - S[SP] <= EP) {
                    S[SP] = 0;
                } else {
                    NP = NP - S[SP];
                    S[SP] = NP;
                }
                break;
            case MARK:
                S[SP+1] = EP;
                S[SP+2] = FP;
                SP += 2;
                break;
            case CALL: {
                int tmp = S[SP];
                S[SP] = PC;
                FP = SP;
                PC = tmp;
                break;
            }
            case SLIDE: {
                int tmp = S[SP];
                SP = SP - instruction.getFirstArg();
                S[SP] = tmp;
                break;
            }
            case ENTER:
                EP = SP + instruction.getFirstArg();
                if (EP >= NP){
                    throw new StackOverflowError("Stack Overflow trying to execute ENTER " + instruction.getFirstArg());
                }
                break;
            case RETURN:
                PC = S[FP];
                EP = S[FP-2];
                if (EP >= NP) {
                    throw new StackOverflowError("Stack Overflow trying to execute RETURN");
                }
                SP = FP-3;
                FP = S[SP+2];
                break;
            case LOADRC:
                SP++;
                S[SP] = FP + instruction.getFirstArg();
                break;
            case LOADR:
                SP++;
                S[SP] = FP + instruction.getFirstArg();
                S[SP] = S[S[SP]];
                break;
            case STORER:
                SP++;
                S[SP] = FP + instruction.getFirstArg();
                S[S[SP]] = S[SP - 1];
                SP--;
                break;
            case HALT:
                break;
            default:
                throw new UnsupportedOperationException("Unknown instruction type: " + instruction.getType());
        }
    }

    // TODO: If you wish, you can implement each instruction as a method
}
