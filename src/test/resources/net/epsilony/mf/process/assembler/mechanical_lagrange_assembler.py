'''

@author: Man YUAN <epsilonyuan@gmail.com>

'''

import mechanical_penalty_assemblier as p_test
import numpy as np

def gen_volume_data(dim, times, normal_nodes_size, lag_nodes_size, rand):
    vol_data = p_test.gen_volume_data(dim, times, normal_nodes_size, rand)
    mats = vol_data['assembledMatries']
    vecs = vol_data['assembledVectors']
    for i in range(len(mats)):
        mat = mats[i]
        whole_size = (normal_nodes_size + lag_nodes_size) * dim
        new_mat = np.zeros((whole_size, whole_size), dtype=np.double)
        new_mat[0:normal_nodes_size * dim, 0:normal_nodes_size * dim] = mat
        mats[i] = new_mat
        
        vec = vecs[i]
        new_vec = np.zeros((whole_size,), dtype=np.double)
        new_vec[0:normal_nodes_size * dim] = vec
        vecs[i] = new_vec
        
        for i in range(normal_nodes_size*dim,whole_size):
            new_mat[i,i]=1
    return vol_data

def gen_lag_sample_indes(size, normal_nodes_size, lag_nodes_size, rand):
    result = p_test.gen_sample_indes(size, lag_nodes_size, rand)
    for i in range(len(result)):
        result[i] += normal_nodes_size
    return result
    
def assemble_lag_dirichlet_mat_vec(dim, lagv, weight, displace, lv, rv):
    left = np.zeros((dim, lagv.shape[1] * dim), np.double)
    right = np.zeros((dim, lagv.shape[1] * dim), np.double)
    lag = np.zeros((dim, lagv.shape[1] * dim), np.double)
    for n, v in zip((left, right, lag), (lv, rv, lagv)):
        for i in range(dim):
            n[i, i::dim] = v[0]
    mat = lag.transpose().dot(right) + left.transpose().dot(lag)
    mat *= weight
    vec = lag.transpose().dot(displace) * weight
            
    return mat, vec

def gen_lagrange_dirichlet_data(dim, vol_mats, vol_vecs, times, normal_nodes_size, lag_nodes_size, rand):
    sizes = [rand.randint(1, normal_nodes_size // 2) for _i in range(times)]
    lag_sizes = [rand.randint(1, lag_nodes_size - 1) for _i in range(times)]
    weights = [rand.random() for _i in range(times)]
    displaces = [np.array([rand.random() for _j in range(dim)], dtype=np.double) for _i in range(times)]

    test_sf_vs = []
    trial_sf_vs = []
    nodes_ids = []
    lag_sf_vs = []
    lag_ids = []
    for size, lag_size in zip(sizes, lag_sizes):
        test_sf_vs.append(p_test.gen_sample_shape_func(dim, size, rand))
        #trial_sf_vs.append(test_sf_vs[-1])
        trial_sf_vs.append(p_test.gen_sample_shape_func(dim, size, rand))
        lag_sf_vs.append(p_test.gen_sample_shape_func(dim, lag_size, rand))
        nodes_ids.append(p_test.gen_sample_indes(size, normal_nodes_size, rand))
        # trial_ids.append(p_test.gen_sample_indes(size, normal_nodes_size, rand))
        lag_ids.append(gen_lag_sample_indes(lag_size, normal_nodes_size, lag_nodes_size, rand))
    asm_mats = []
    asm_vecs = []
    all_nodes_size = normal_nodes_size + lag_nodes_size
    for weight, displace, test, nodes_id, trial, lag, lag_id in zip(weights, displaces, test_sf_vs, nodes_ids, trial_sf_vs, lag_sf_vs, lag_ids):
        lv = p_test.to_whole_vector(test, nodes_id, all_nodes_size)
        rv = p_test.to_whole_vector(trial, nodes_id, all_nodes_size)
        lagv = p_test.to_whole_vector(lag, lag_id, all_nodes_size)
        
        mat, vec = assemble_lag_dirichlet_mat_vec(dim, lagv, weight, displace, lv, rv)
        if len(asm_mats) > 0:
            asm_mats.append(asm_mats[-1] + mat)
            asm_vecs.append(asm_vecs[-1] + vec)
        else:
            asm_mats.append(mat + vol_mats[-1])
            asm_vecs.append(vec + vol_vecs[-1])
            
        for ld in lag_id:
            for dd in range(dim):
                asm_mats[-1][ld*dim+dd][ld*dim+dd]=0
    return {'dim':dim,
            'lagNodesSize':lag_nodes_size,
            'nodesSize':normal_nodes_size,
            'weights':weights,
            'testShapeFuncValuesArray':test_sf_vs,
            'trialShapeFuncValuesArray':trial_sf_vs,
            'lagShapeFuncValuesArray':lag_sf_vs,
            'nodesAssemblyIndesArray':nodes_ids,
            'lagAssemblyIndesArray':lag_ids,
            'assembledMatries':asm_mats,
            'assembledVectors':asm_vecs,
            'method':'dirichlet',
            'loads':displaces,
            'testOrder':1
            }

def gen_neumann_data(dim, dir_vecs, times, normal_nodes_size, rand):
    fake_vecs = [np.zeros((dim * normal_nodes_size,), dtype=np.double)]
    result = p_test.gen_neumann_data(dim, fake_vecs, times, normal_nodes_size, rand)
    vecs = result['assembledVectors']
    for i in range(len(vecs)):
        v = vecs[i]
        new_v = dir_vecs[-1].copy()
        new_v[0:len(v)] += v
        vecs[i] = new_v
    return result

def gen_test_datas():
    random_seed = 1147
    times = 3;
    normal_nodes_size = 10
    lag_nodes_size = 4
    result = []
    for dim in range(1, 4):
        import random as rand
        rand.seed(random_seed)
        vol_data = gen_volume_data(dim, times, normal_nodes_size, lag_nodes_size, rand)
        dirichlet_data = gen_lagrange_dirichlet_data(dim, vol_data['assembledMatries'], vol_data['assembledVectors'], times, normal_nodes_size, lag_nodes_size, rand)
        neumann_data = gen_neumann_data(dim, dirichlet_data['assembledVectors'], times, normal_nodes_size, rand)
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
    sio = StringIO()
    import json
    json.dump(gen_test_datas(), sio, indent=4, cls=NumpyEncoder)
    return sio.getvalue()

if __name__ == "__main__":
    print(gen_test_data_json_string())
