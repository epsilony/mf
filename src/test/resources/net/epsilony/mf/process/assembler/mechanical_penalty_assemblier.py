'''

@author: Man YUAN <epsilonyuan@gmail.com>

'''

import numpy as np

def gen_constitutive(dim, rand):
    sizes = (1, 3, 6)
    size = sizes[dim - 1]
    result = np.ndarray((size, size), dtype=np.double)
    for i in range(size):
        for j in range(i, size):
            result[i][j] = rand.random()
            result[j][i] = result[i][j]
    return result

def gen_sample_shape_func(dim, size, rand):
    result = np.ndarray((dim + 1, size), dtype=np.double)
    for i in range(result.shape[0]):
        for j in range(result.shape[1]):
            result[i][j] = rand.random()
    return result;

def gen_sample_indes(size, hole_size, rand):
    all_indes = [i for i in range(hole_size)]
    for i in range(hole_size):
        exchange_indes = rand.randint(0, hole_size - 1)
        t = all_indes[i]
        all_indes[i] = all_indes[exchange_indes]
        all_indes[exchange_indes] = t
    return [all_indes[i] for i in range(size)]

def to_whole_vector(shape_func, indes, all_nodes_size):
    result = np.zeros((shape_func.shape[0], all_nodes_size), dtype=np.double)
    for i in range(len(indes)):
        result[:, indes[i]] = shape_func[:, i]
    return result

def assemble_volume_mat_1d(test_shape_func, c_law, trial_shape_func):
    return c_law * np.dot(
                              test_shape_func[0].reshape(test_shape_func[0].shape[0], 1),
                              trial_shape_func[0].reshape((1, trial_shape_func[0].shape[0])))

def assemble_volume_mat_2d(test_shape_func_whole, c_law, trial_shape_func_whole):
    left = np.zeros((3, 2 * test_shape_func_whole.shape[1]), dtype=np.double)
    right = np.zeros((3, 2 * trial_shape_func_whole.shape[1]), dtype=np.double)
    for n, shape_func in zip((left, right), (test_shape_func_whole, trial_shape_func_whole)):
        n[0, 0::2] = shape_func[1]
        n[1, 1::2] = shape_func[2]
        n[2, 0::2] = shape_func[2]
        n[2, 1::2] = shape_func[1]
    
    return left.transpose().dot(c_law).dot(right)

def assemble_volume_mat_3d(test_shape_func_whole, c_law, trial_shape_func_whole):
    left = np.zeros((6, 3 * test_shape_func_whole[0].shape[0]), dtype=np.double)
    right = np.zeros((6, 3 * trial_shape_func_whole[0].shape[0]), dtype=np.double)
    
    for n, shape_func in zip((left, right), (test_shape_func_whole, trial_shape_func_whole)):
        n[0, 0::3] = shape_func[1]
        n[1, 1::3] = shape_func[2]
        n[2, 2::3] = shape_func[3]
        n[3, 0::3] = shape_func[2]
        n[3, 1::3] = shape_func[1]
        n[4, 1::3] = shape_func[3]
        n[4, 2::3] = shape_func[2]
        n[5, 0::3] = shape_func[3]
        n[5, 2::3] = shape_func[1]

    return left.transpose().dot(c_law).dot(right)

_assemble_volume_mat = (assemble_volume_mat_1d, assemble_volume_mat_2d, assemble_volume_mat_3d)

def assemble_volume_mat(dim, weight, test_shape_func_whole, c_law, trial_shape_func_whole):
    f = _assemble_volume_mat[dim - 1]
    return weight * f(test_shape_func_whole, c_law, trial_shape_func_whole)

def assemble_volume_or_nature_vec(dim, weight, volume_force, test_shape_func_whole):
    result = np.zeros((dim * test_shape_func_whole[0].shape[0],), dtype=np.double)
    for i in range(dim):
        result[i::dim] = weight * volume_force[i] * test_shape_func_whole[0]
    return result
    

def gen_volume_data(dim, times, all_nodes_size, rand):
    c_law = gen_constitutive(dim, rand)
    sizes = [rand.randint(1, all_nodes_size // 2) for _i in range(times)]
    weights = [rand.random() for _i in range(times)]
    volume_forces = [np.array([rand.random() for _j in range(dim)], dtype=np.double) for _i in range(times)]
    
    test_sf_vs = []
    trial_sf_vs = []
    nodes_ids = []
    for size in sizes:
        test_sf_vs.append(gen_sample_shape_func(dim, size, rand))
        trial_sf_vs.append(gen_sample_shape_func(dim, size, rand))
        nodes_ids.append(gen_sample_indes(size, all_nodes_size, rand))
    asm_mats = []
    asm_vecs = []
    for weight, vol_force, test, nodes_id, trial in zip(weights, volume_forces, test_sf_vs, nodes_ids, trial_sf_vs):
        lv = to_whole_vector(test, nodes_id, all_nodes_size)
        rv = to_whole_vector(trial, nodes_id, all_nodes_size)
        
        mat = assemble_volume_mat(dim, weight, lv, c_law, rv)
        if len(asm_mats) > 0:
            asm_mats.append(asm_mats[-1] + mat)
        else:
            asm_mats.append(mat)
        
        vec = assemble_volume_or_nature_vec(dim, weight, vol_force, lv)
        if len(asm_vecs) > 0:   
            asm_vecs.append(asm_vecs[-1] + vec)
        else:
            asm_vecs.append(vec)
    return {'dim':dim,
            'constitutiveLaw':c_law,
            'nodesSize':all_nodes_size,
            'weights':weights,
            'testShapeFuncValuesArray':test_sf_vs,
            'trialShapeFuncValuesArray':trial_sf_vs,
            'nodesAssemblyIndesArray':nodes_ids,
            'assembledMatries':asm_mats,
            'assembledVectors':asm_vecs,
            'loads':volume_forces,
            'method':'volume',
            'testOrder':0
            }
def assemble_penalty_dirichlet_mat_vec(dim, penalty, weight, displace, test_shape_func_whole, trial_shape_func_whole):
    left = np.zeros((dim, test_shape_func_whole.shape[1] * dim), dtype=np.double)
    right = np.zeros((dim, trial_shape_func_whole.shape[1] * dim), dtype=np.double)
    for n, v in zip((left, right), (test_shape_func_whole, trial_shape_func_whole)):
        for i in range(dim):
            n[i, i::dim] = v[0]
    mat = left.transpose().dot(right) * weight * penalty
    vec = left.transpose().dot(displace) * weight * penalty
    return (mat, vec)
    
def gen_penalty_dirichlet_data(dim, vol_asm_mats, vol_asm_vecs, times, all_nodes_size, rand):
    sizes = [rand.randint(1, all_nodes_size // 2) for _i in range(times)]
    weights = [rand.random() for _i in range(times)]
    displaces = [np.array([rand.random() for _j in range(dim)], dtype=np.double) for _i in range(times)]
    penalty = rand.random() * 1e3
    test_sf_vs = []
    trial_sf_vs = []
    nodes_ids = []
    for size in sizes:
        test_sf_vs.append(gen_sample_shape_func(dim, size, rand))
        trial_sf_vs.append(gen_sample_shape_func(dim, size, rand))
        #trial_sf_vs.append(test_sf_vs[-1])
        nodes_ids.append(gen_sample_indes(size, all_nodes_size, rand))
        #trial_ids.append(nodes_ids[-1])
    asm_mats = []
    asm_vecs = []
    for weight, displace, test, nodes_id, trial in zip(weights, displaces, test_sf_vs, nodes_ids, trial_sf_vs):
        lv = to_whole_vector(test, nodes_id, all_nodes_size)
        rv = to_whole_vector(trial, nodes_id, all_nodes_size)
        
        mat, vec = assemble_penalty_dirichlet_mat_vec(dim, penalty, weight, displace, lv, rv)
        if len(asm_mats) > 0:
            asm_mats.append(asm_mats[-1] + mat)
            asm_vecs.append(asm_vecs[-1] + vec)
        else:
            asm_mats.append(mat + vol_asm_mats[-1])
            asm_vecs.append(vec + vol_asm_vecs[-1])
    return {'dim':dim,
            'penalty':penalty,
            'nodesSize':all_nodes_size,
            'weights':weights,
            'testShapeFuncValuesArray':test_sf_vs,
            'trialShapeFuncValuesArray':trial_sf_vs,
            'nodesAssemblyIndesArray':nodes_ids,
            'assembledMatries':asm_mats,
            'assembledVectors':asm_vecs,
            'method':'dirichlet',
            'loads':displaces,
            'testOrder':1
            }
    pass

def gen_neumann_data(dim, dir_vecs, times, all_nodes_size, rand):
    sizes = [rand.randint(1, all_nodes_size // 2) for _i in range(times)]
    weights = [rand.random() for _i in range(times)]
    tractions = [np.array([rand.random() for _j in range(dim)], dtype=np.double) for _i in range(times)]
    
    test_sf_vs = []
    test_ids = []
    for size in sizes:
        test_sf_vs.append(gen_sample_shape_func(dim, size, rand))
        test_ids.append(gen_sample_indes(size, all_nodes_size, rand))

    asm_vecs = []
    for weight, force, test, test_id in zip(weights, tractions, test_sf_vs, test_ids):
        lv = to_whole_vector(test, test_id, all_nodes_size)
      
        vec = assemble_volume_or_nature_vec(dim, weight, force, lv)
        if len(asm_vecs) > 0:
            asm_vecs.append(asm_vecs[-1] + vec)
        else:
            asm_vecs.append(vec + dir_vecs[-1])
    return {'dim':dim,
            'nodesSize':all_nodes_size,
            'weights':weights,
            'testShapeFuncValuesArray':test_sf_vs,
            'nodesAssemblyIndesArray':test_ids,
            'assembledVectors':asm_vecs,
            'method':'neumann',
            'loads':tractions,
            'testOrder':2
            }

def gen_test_datas():
    random_seed = 1147
    times = 3;
    whole_size = 10
    result = []
    for dim in range(1, 4):
        import random as rand
        rand.seed(random_seed)
        vol_data = gen_volume_data(dim, times, whole_size, rand)
        dirichlet_data = gen_penalty_dirichlet_data(dim, vol_data['assembledMatries'], vol_data['assembledVectors'], times, whole_size, rand)
        neumann_data = gen_neumann_data(dim, dirichlet_data['assembledVectors'], times, whole_size, rand)
        result.append({'dim':dim, 'data':[vol_data, dirichlet_data, neumann_data]})
    return result

from json import JSONEncoder

class NumpyEncoder(JSONEncoder):
    def default(self, obj):
        if isinstance(obj, np.ndarray):
            return obj.tolist()
        return super().default(obj)
    
def gen_test_data_json_file():
    file_name = "mechanical_penalty_assemblier.json"
    with open(file_name, 'w') as fop:
        import json
        json.dump(gen_test_datas(), fop, indent=4, cls=NumpyEncoder)

def gen_test_data_json_string():
    from io import StringIO
    sio=StringIO()
    import json
    json.dump(gen_test_datas(),sio,indent=4,cls=NumpyEncoder)
    return sio.getvalue()

if __name__ == "__main__":
    print(gen_test_data_json_string())
