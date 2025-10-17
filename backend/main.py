from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from collections import defaultdict, deque

app = FastAPI()

app.add_middleware(CORSMiddleware, allow_origins=["*"], allow_methods=["*"], allow_headers=["*"])

@app.get('/')
def read_root():
    return {'hiphip': 'hurray'}

@app.post('/pipelines/parse')
def parse_pipeline(data: dict):
    nodes = data.get('nodes', [])
    edges = data.get('edges', [])
    # Build graph and check DAG using Kahn's algorithm
    graph = defaultdict(list)
    in_deg = defaultdict(int)
    node_ids = {n['id'] for n in nodes}
    
    for n in node_ids:
        in_deg[n] = 0
    
    for e in edges:
        if e['source'] in node_ids and e['target'] in node_ids:
            graph[e['source']].append(e['target'])
            in_deg[e['target']] += 1
    
    q = deque([n for n in node_ids if in_deg[n] == 0])
    count = 0
    
    while q:
        curr = q.popleft()
        count += 1
        for neighbor in graph[curr]:
            in_deg[neighbor] -= 1
            if in_deg[neighbor] == 0:
                q.append(neighbor)
    
    return {'num_nodes': len(nodes), 'num_edges': len(edges), 'is_dag': count == len(node_ids)}