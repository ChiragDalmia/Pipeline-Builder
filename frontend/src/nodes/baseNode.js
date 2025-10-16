import { Handle, Position } from "reactflow";

export const BaseNode = ({ id, data, config }) => {
  const { title, fields = [], handles = [], width = 200, height = 80 } = config;

  return (
    <div
      style={{
        minWidth: width,
        maxWidth: 400,
        minHeight: height,
        border: "1.5px solid #d0d0d0",
        borderRadius: "8px",
        backgroundColor: "#ffffff",
        padding: "12px",
        boxShadow: "0 2px 4px rgba(0, 0, 0, 0.08)",
        transition: "box-shadow 0.2s ease, border-color 0.2s ease",
        display: "flex",
        flexDirection: "column",
      }}
    >
      {/* Input Handles */}
      {handles
        .filter((h) => h.type === "target")
        .map((h) => (
          <Handle
            key={h.id}
            type="target"
            position={Position.Left}
            id={`${id}-${h.id}`}
            style={{
              width: "10px",
              height: "10px",
              backgroundColor: "#ffffff",
              border: "2px solid #6b9bd1",
              borderRadius: "50%",
              ...h.style,
            }}
          />
        ))}
      {/* Title */}
      <div
        style={{
          fontWeight: "600",
          fontSize: "14px",
          color: "#1e1e1e",
          marginBottom: "8px",
          borderBottom: "1px solid #f0f0f0",
          paddingBottom: "6px",
        }}
      >
        <span>{title}</span>
      </div>
      {/* Fields */}
      <div style={{ display: "flex", flexDirection: "column", gap: "6px" }}>
        {fields.map((field, idx) => (
          <label
            key={idx}
            style={{
              fontSize: "12px",
              color: "#4a4a4a",
              display: "flex",
              flexDirection: "column",
              gap: "2px",
            }}
          >
            {field.label}:{field.render(data, id)}
          </label>
        ))}
      </div>
      {/* Output Handles */}
      {handles
        .filter((h) => h.type === "source")
        .map((h) => (
          <Handle
            key={h.id}
            type="source"
            position={Position.Right}
            id={`${id}-${h.id}`}
            style={{
              width: "10px",
              height: "10px",
              backgroundColor: "#ffffff",
              border: "2px solid #6b9bd1",
              borderRadius: "50%",
              ...h.style,
            }}
          />
        ))}
    </div>
  );
};
