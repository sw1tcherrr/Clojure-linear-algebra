# Clojure-linear-algebra

Functions for working with linear algebra objects:

- scalars – numbers (`1`)
- vectors – vectors of numbers (`[1, 2, 3]`)
- matrices – vectors of vectors (`[[1, 2], [3, 4], [5, 6]]`)
- tensors – multidimensional tables of numbers (`[[[2 3 4] [5 6 7]]]`)

All functions check their preconditions (e.g., can’t add vectors of different length)

All functions accept any number of arguments (e.g, `(v+ [1 2] [3 4] [5 6])` gives `[9 12]`)

Code repetition is minimized by using higher order functions

Vector functions:

- `v+`/`v-`/`v*`/`vd`  coordinatewise adddition/subtraction/multiplication/division
- `scalar`/`vect`  – dot product/cross pproduct
- `v*s` – multiplication by scalar

 Matrix functions:

- `m+`/`m-`/`m*`/`md`  elementwise adddition/subtraction/multiplication/division
- `m*s` – multiplication by scalar
- `m*v` – multiplication by vector
- `m*m` – matrix multiplication
- `transpose` – transpose

Tensor functions:
- `tb+`/`tb-`/`tb*`/`tbd` elementwise adddition/subtraction/multiplication/division
  If arguments don't share the same shape – the less ones are broadcasted to the bigger ones’ shape 
  
  (e.g., `(tb+ 1 [[10 20 30] [40 50 60]] [100 200])` gives `[[111 121 131] [241 251 261]]`)

Tensor shape – sequence (_s_<sub>1</sub>, _s_<sub>2</sub>, …, _s<sub>n</sub>_), where _n_ – number of axes,  _s<sub>i</sub>_ – number of elements on _i_-th axis 

(e.g., shape of  `[[[2 3 4] [5 6 7]]]`  is `(1, 2, 3)`, shape of  `1` is `()`)

Tensor of shape (_s_<sub>1.._n_</sub>) can be _broadcasted_ to the tensor of shape (_u_<sub>1.._m_</sub>), if (_s_<sub>i.._n_</sub>) is a prefix of (_u<sub>1..m</sub>_)

Elements are copied on the missing axes

(`[[1 2]]` from `(1, 2)` to `(1, 2, 3)` gives `[[[1 1 1] [2 2 2]]]`,
`1` from `()` to `(2, 3)` gives `[[1 1 1] [1 1 1]]`)
