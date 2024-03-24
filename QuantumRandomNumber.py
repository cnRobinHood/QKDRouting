import qiskit
from qiskit import IBMQ
import math
import sys

_circuit = None
_bitCache = ''

def set_provider_as_IBMQ(token: str = None):

    global provider
    if token == None or '':
        provider = qiskit.BasicAer
    else:
        IBMQ.save_account(token)
        IBMQ.load_account()
        provider = IBMQ.get_provider('ibm-q')

def _set_qubits(n):
    global _circuit
    qr = qiskit.QuantumRegister(n)
    cr = qiskit.ClassicalRegister(n)
    _circuit = qiskit.QuantumCircuit(qr, cr)
    _circuit.h(qr)
    _circuit.measure(qr,cr)

_set_qubits(8)

def set_backend(backend: str = 'qasm_simulator'):
    global _backend
    global provider
    available_backends = provider.backends(backend, filters = lambda x: x.status().operational == True)
    if (backend != '') and (backend in str(available_backends)):
        _backend = provider.get_backend(backend)
    else:
        print(str(backend)+' is not available. Backend is set to qasm_simulator.')
        _backend = qiskit.BasicAer.get_backend('qasm_simulator')
    _set_qubits(_backend.configuration().n_qubits)

def _bit_from_counts(counts):
    return [k for k, v in counts.items() if v == 1][0]

def _request_bits(n):
    global _bitCache
    iterations = math.ceil(n/_circuit.width()*2)
    for _ in range(iterations):

        job = qiskit.execute(_circuit, _backend, shots=1)
        _bitCache += _bit_from_counts(job.result().get_counts())

def get_bit_string(n: int) -> str:

    global _bitCache
    if len(_bitCache) < n:
        _request_bits(n-len(_bitCache))
    bitString = _bitCache[0:n]
    _bitCache = _bitCache[n:]
    return bitString


def get_random_int(min: int, max: int) -> int:

    delta = max-min
    n = math.floor(math.log(delta,2))+1
    result = int(get_bit_string(n),2)
    while(result > delta):
        result = int(get_bit_string(n),2)
    result += min
    return result
num_args = len(sys.argv)
set_provider_as_IBMQ()
set_backend()
random_number = get_random_int(0,int(sys.argv[1])-1)
print(random_number)

