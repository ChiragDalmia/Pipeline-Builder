"use client";

// ui.js
// Displays the drag-and-drop UI
// --------------------------------------------------

import { useState, useRef, useCallback } from "react";
import ReactFlow, { Controls, Background, MiniMap } from "reactflow";
import { useStore } from "./store";
import { shallow } from "zustand/shallow";
import { InputNode } from "./nodes/inputNode";
import { LLMNode } from "./nodes/llmNode";
import { OutputNode } from "./nodes/outputNode";
import { TextNode } from "./nodes/textNode";
import { MathNode } from "./nodes/mathNode";
import { FilterNode } from "./nodes/filterNode";
import { TransformNode } from "./nodes/transformNode";
import { ConditionalNode } from "./nodes/conditionalNode";
import { ApiNode } from "./nodes/apiNode";
import "reactflow/dist/style.css";

const gridSize = 20;
const proOptions = { hideAttribution: true };
const nodeTypes = {
  customInput: InputNode,
  llm: LLMNode,
  customOutput: OutputNode,
  text: TextNode,
  math: MathNode,
  filter: FilterNode,
  transform: TransformNode,
  conditional: ConditionalNode,
  api: ApiNode,
};

const selector = (s) => ({
  nodes: s.nodes,
  edges: s.edges,
  getNodeID: s.getNodeID,
  addNode: s.addNode,
  onNodesChange: s.onNodesChange,
  onEdgesChange: s.onEdgesChange,
  onConnect: s.onConnect,
});

export const PipelineUI = () => {
  const ref = useRef(null);
  const [instance, setInstance] = useState(null);
  const {
    nodes,
    edges,
    getNodeID,
    addNode,
    onNodesChange,
    onEdgesChange,
    onConnect,
  } = useStore(selector, shallow);

  const onDrop = useCallback(
    (e) => {
      e.preventDefault();
      const bounds = ref.current.getBoundingClientRect();
      const data = e?.dataTransfer?.getData("application/reactflow");
      if (data) {
        const { nodeType: type } = JSON.parse(data);
        if (!type) return;

        const position = instance.project({
          x: e.clientX - bounds.left,
          y: e.clientY - bounds.top,
        });

        const id = getNodeID(type);
        addNode({ id, type, position, data: { id, nodeType: type } });
      }
    },
    [instance, addNode, getNodeID]
  );

  const onDragOver = useCallback((e) => {
    e.preventDefault();
    e.dataTransfer.dropEffect = "move";
  }, []);

  const controlStyle = {
    border: "1px solid #e0e0e0",
    borderRadius: "8px",
    boxShadow: "0 2px 8px rgba(0, 0, 0, 0.08)",
    backgroundColor: "#ffffff",
    overflow: "hidden",
  };

  return (
    <div
      ref={ref}
      style={{ width: "100vw", height: "70vh", backgroundColor: "#ffffff" }}
    >
      <ReactFlow
        nodes={nodes}
        edges={edges}
        onNodesChange={onNodesChange}
        onEdgesChange={onEdgesChange}
        onConnect={onConnect}
        onDrop={onDrop}
        onDragOver={onDragOver}
        onInit={setInstance}
        nodeTypes={nodeTypes}
        proOptions={proOptions}
        snapGrid={[gridSize, gridSize]}
        connectionLineType="smoothstep"
      >
        <Background bgColor="#f7f9fb" />
        <Controls style={controlStyle} />
        <MiniMap
          style={{ ...controlStyle, backgroundColor: "#fafafa" }}
          maskColor="rgba(0, 0, 0, 0.05)"
          nodeColor="#ffffff"
          nodeStrokeColor="#d0d0d0"
          nodeBorderRadius={4}
          pannable
          zoomable
        />
      </ReactFlow>
    </div>
  );
};
