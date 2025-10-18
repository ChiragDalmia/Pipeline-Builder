# Pipeline Builder - Quick Start

## Setup & Run

### Backend
```bash
cd backend
pip install fastapi uvicorn
uvicorn main:app --reload
```

### Frontend
```bash
cd frontend
npm install
npm start
```

## Usage

1. **Add Nodes**: Click toolbar buttons to add nodes to canvas
2. **Connect**: Drag from one node's handle to another
3. **Submit**: Click Submit to analyze pipeline

## What is DAG?

- ✅ **Valid DAG**: No cycles (Input → LLM → Output)
- ❌ **Invalid**: Has cycles (A → B → A)

## Test Examples

**Valid**: Input → Text → Output  
**Invalid**: Create A → B → A loop

---

Built with React, ReactFlow, FastAPI