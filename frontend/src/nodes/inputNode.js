import { useState } from "react";
import { BaseNode } from "./baseNode";

export const InputNode = ({ id, data }) => {
  const [currName, setCurrName] = useState(
    data?.inputName || id.replace("customInput-", "input_")
  );
  const [inputType, setInputType] = useState(data.inputType || "Text");

  const config = {
    title: "Input",
    fields: [
      {
        label: "Name",
        render: () => (
          <input
            type="text"
            value={currName}
            onChange={(e) => setCurrName(e.target.value)}
          />
        ),
      },
      {
        label: "Type",
        render: () => (
          <select
            value={inputType}
            onChange={(e) => setInputType(e.target.value)}
          >
            <option value="Text">Text</option>
            <option value="File">File</option>
          </select>
        ),
      },
    ],
    handles: [{ type: "source", id: "value" }],
  };

  return <BaseNode id={id} data={data} config={config} />;
};
