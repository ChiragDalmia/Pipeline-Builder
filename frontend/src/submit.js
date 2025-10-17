"use client";

import { useStore } from "./store";

export const SubmitButton = () => {
  const { nodes, edges } = useStore((state) => ({
    nodes: state.nodes,
    edges: state.edges,
  }));

  const handleSubmit = async () => {
    try {
      const res = await fetch("http://localhost:8000/pipelines/parse", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ nodes, edges }),
      });

      const { num_nodes, num_edges, is_dag } = await res.json();
      console.log({ num_nodes, num_edges, is_dag });
      alert(
        `Nodes: ${num_nodes}\nEdges: ${num_edges}\nDAG: ${is_dag ? "✓" : "✗"}`
      );
    } catch (err) {
      alert(`Error: ${err.message}`);
    }
  };

  return (
    <div
      style={{
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        padding: "16px",
      }}
    >
      <button
        onClick={handleSubmit}
        style={{
          padding: "10px 24px",
          backgroundColor: "#fff",
          border: "1.5px solid #d0d0d0",
          borderRadius: "8px",
          fontSize: "14px",
          fontWeight: "500",
          cursor: "pointer",
          transition: "all 0.2s",
        }}
        onMouseEnter={(e) => (e.target.style.backgroundColor = "#f5f5f5")}
        onMouseLeave={(e) => (e.target.style.backgroundColor = "#fff")}
      >
        Submit
      </button>
    </div>
  );
};
