import { useState } from "react";
import { BaseNode } from "./baseNode";

export const OutputNode = ({ id, data }) => {
  const [currName, setCurrName] = useState(
    data?.outputName || id.replace("customOutput-", "output_")
  );
  const [outputType, setOutputType] = useState(data.outputType || "Text");

  const config = {
    title: "Output",
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
            value={outputType}
            onChange={(e) => setOutputType(e.target.value)}
          >
            <option value="Text">Text</option>
            <option value="Image">Image</option>
          </select>
        ),
      },
    ],
    handles: [{ type: "target", id: "value" }],
  };

  return <BaseNode id={id} data={data} config={config} />;
};
