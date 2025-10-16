"use client";

// draggableNode.js

export const DraggableNode = ({ type, label }) => {
  const onDragStart = (event, nodeType) => {
    const appData = { nodeType };
    event.target.style.cursor = "grabbing";
    event.dataTransfer.setData(
      "application/reactflow",
      JSON.stringify(appData)
    );
    event.dataTransfer.effectAllowed = "move";
  };

  return (
    <div
      className={type}
      onDragStart={(event) => onDragStart(event, type)}
      onDragEnd={(event) => (event.target.style.cursor = "grab")}
      style={{
        cursor: "grab",
        minWidth: "80px",
        height: "60px",
        display: "flex",
        alignItems: "center",
        borderRadius: "8px",
        backgroundColor: "#ffffff",
        border: "1.5px solid #e0e0e0",
        justifyContent: "center",
        flexDirection: "column",
        padding: "8px",
        boxShadow: "0 1px 3px rgba(0, 0, 0, 0.08)",
        transition: "all 0.2s ease",
      }}
      onMouseEnter={(e) => {
        e.currentTarget.style.boxShadow = "0 2px 6px rgba(0, 0, 0, 0.12)";
        e.currentTarget.style.borderColor = "#d0d0d0";
      }}
      onMouseLeave={(e) => {
        e.currentTarget.style.boxShadow = "0 1px 3px rgba(0, 0, 0, 0.08)";
        e.currentTarget.style.borderColor = "#e0e0e0";
      }}
      draggable
    >
      <span style={{ color: "#1e1e1e", fontSize: "13px", fontWeight: "500" }}>
        {label}
      </span>
    </div>
  );
};
