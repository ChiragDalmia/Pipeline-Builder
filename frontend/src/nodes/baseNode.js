import { Handle, Position } from "reactflow";

export const BaseNode = ({ id, data, config }) => {
  const { title, fields = [], handles = [], width = 200, height = 80 } = config;
  const targetHandles = handles.filter((h) => h.type === "target");
  const sourceHandles = handles.filter((h) => h.type === "source");

  return (
    <div
      style={{
        minWidth: width,
        maxWidth: 400,
        minHeight: height,
        border: "1.5px solid #d0d0d0",
        borderRadius: "8px",
        backgroundColor: "#fff",
        padding: "12px",
        boxShadow: "0 2px 4px rgba(0,0,0,.08)",
        transition: "box-shadow .2s ease, border-color .2s ease",
        display: "flex",
        flexDirection: "column",
        position: "relative",
      }}
    >
      {targetHandles.map((h, idx) => {
        const topPosition = (height / (targetHandles.length + 1)) * (idx + 1);
        return (
          <div
            key={h.id}
            style={{
              position: "absolute",
              left: "-8px",
              top: `${topPosition}px`,
            }}
          >
            <Handle
              type="target"
              position={Position.Left}
              id={`${id}-${h.id}`}
              style={{
                width: "10px",
                height: "10px",
                backgroundColor: "#fff",
                border: "2px solid #6b9bd1",
                borderRadius: "50%",
                ...h.style,
              }}
            />
            {h.label && (
              <div
                style={{
                  position: "absolute",
                  left: "-5px",
                  top: "50%",
                  transform: "translate(-100%,-50%)",
                  fontSize: "10px",
                  color: "#6b6b6b",
                  backgroundColor: "#f7f9fb",
                  padding: "2px 6px",
                  borderRadius: "4px",
                  whiteSpace: "nowrap",
                  border: "1px solid #e0e0e0",
                }}
              >
                {h.label}
              </div>
            )}
          </div>
        );
      })}
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
      <div
        style={{
          display: "flex",
          flexDirection: "column",
          gap: "6px",
          flex: 1,
        }}
      >
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
      {sourceHandles.map((h, idx) => {
        const topPosition = (height / (sourceHandles.length + 1)) * (idx + 1);
        return (
          <div
            key={h.id}
            style={{
              position: "absolute",
              right: "-8px",
              top: `${topPosition}px`,
            }}
          >
            <Handle
              type="source"
              position={Position.Right}
              id={`${id}-${h.id}`}
              style={{
                width: "10px",
                height: "10px",
                backgroundColor: "#fff",
                border: "2px solid #6b9bd1",
                borderRadius: "50%",
                ...h.style,
              }}
            />
          </div>
        );
      })}
    </div>
  );
};
